$(function(){


// webSocket服务器地址
    var urlPrefix ='ws://192.168.1.23:8080/game/kid';
// 服务器交互工具
    var ws = null;
// 返回对象
    var response = null;
// 玩家
    var players = null;
    var player = null;
// 玩家手牌
    var hand = null;


// 候选牌
    var play = new Array();
// 声明牌
    var state = null;
// 玩家昵称
    var username = null;
// 离线玩家
    var offlineNumber = 0;


// 登录
    $("#login").click(function(){
        username = $('#username input').val()==""?"未命名"+Math.floor(10086*Math.random()):$('#username input').val();
        $('#username input').val(username);

        var url = urlPrefix + "?username=" + username;
        ws = new WebSocket(url);
        ws.onopen = function () {
            $("#login").hide();
            $("#username input").attr("disabled","disabled");
            $("#startNumber, #matching").show();

            if (ws){
                $("#login").hide();
                window.setInterval(function(){
                    var ping = "ping";
                    ws.send(ping);
                },30000);
                console.log("建立 websocket 连接...");
            }
        };
    });


// 匹配
    $('#matching').click(function(){
        var startNumber = $('#startNumber input').val()==""?2:$('#startNumber input').val();
        ws.send("username=" + username + "&operateType=-1" + "&startNumber=" + startNumber);

        $("#startNumber, #matching").hide();

        ws.onmessage = function(event){
            response = $.parseJSON(event.data);

            // 匹配
            if (response.status == 0){
                var schedule = "当前人数:" + response.players.length + "/" + response.startNumber + "&nbsp 匹配中...";
                $("#schedule").html(schedule);
            }

            // 对局
            if (response.status == 1){
                players = response.players;

                for(var j = 0;j < players.length;j++){
                    if(players[j].username == username){
                        player = players[j];
                        hand = player.hand;
                    }
                }

                // 消息
                talk();

                // 角逐
                victory();

            }

        };
    });


    function talk() {

        var talkHtml = "";
        var talkArr = response.talk;
        for(var i = 0;i < talkArr.length;i++){
            talkHtml = talkHtml + talkArr[i] + "<br/>";
            if(talkArr[i].indexOf("退出游戏") != -1){
                offlineNumber++;
            }
        }

        $("#talkDiv").html(talkHtml);
    }

    function victory() {

        if (hand.length == 0) {
            $("#hand").html();
            if (response.current == username) {
                over();
                alert("胜利No." + (response.startNumber - offlineNumber - players.length + 1));
                ws.send("username=" + username + "&operateType=3&grid=" + response.id);
            }
            return ;
        }


        if (players.length == 1) {
            over();
            if (offlineNumber != 0) {
                alert("胜利No." + (response.startNumber - offlineNumber - players.length + 1));
            } else {
                alert("胜败乃兵家常事,大侠请重新来过");
                ws.send("username=" + username + "&operateType=4&grid=" + response.id);
            }
            return ;
        }

        painting();
    }

    function painting() {

        var schedule = "当前人数:" + response.players.length + "/" + response.startNumber + " &nbsp 对局中...";

        $("#schedule").html(schedule);
        players = response.players;

        // 按钮
        actionBtn();

        // 桌牌
        desk();

        // 手牌
        hand_();

        // 候选
        candidate();
    }

// 按钮
    function actionBtn() {
        $("#takeOutBtn, #stateDiv, #exposeBtn, #passBtn, #wait").hide();
        if (response.current == username){
            $("#takeOutBtn").show();
            if (response.state == null){
                $("#stateDiv span").css("background-color","");
                $("#stateDiv").show();
            }
            if (response.desk.length > 0){
                $("#exposeBtn").show();
                $("#passBtn").show();
            }
        }else {
            $("#wait").show();
        };
    }

    function desk() {
        var desk = response.desk;
        if (desk.length > 0){
            $("#desk").html(desk.length + "*" + response.state);
        }else {
            $("#desk").html("wait");
        }
    }

    function hand_() {

        var handSort = new Array();
        for(var i = 1;i < 14;i++){
            var temp = new Array();
            for(var j = 0;j < hand.length;j++){
                if(hand[j] == i || hand[j] == i + 13 || hand[j] == i + 26 || hand[j] == i + 39){
                    temp.push(hand[j]);
                }
            }
            temp = temp.sort(function(a,b){
                return a-b;
            });
            handSort = handSort.concat(temp);
        }

        var handCode = "";
        handCode = handCode+ "<div class=handOne cardNumber="+ handSort[0] +">"
            +flower(handSort[0])+"</div>";
        for(var i = 1;i < handSort.length;i++){
            handCode = handCode+"<div class=hand cardNumber="+ handSort[i] +">"
                +flower(handSort[i])+"</div>";
        }
        $("#hand").html(handCode);
    }

    function candidate() {

        $(".hand, .handOne").click(function () {
            if (response.current == username){
                var bgcolor = $(this).css("background-color");
                var cardNumber = $(this).attr("cardNumber");

                if (bgcolor == "rgb(0, 247, 222)"){
                    if (play.length > 3){
                        $(".hand, .handOne").each(function (i) {
                            if ($(this).attr("cardNumber") == play[0]){
                                $(this).css("background-color","#00F7DE");
                                return false;
                            };
                        });
                        play.splice(0,1);
                    }
                    play.push(cardNumber);
                    $(this).css("background-color","#ffff00")
                }

                if (bgcolor == "rgb(255, 255, 0)"){
                    play.splice($.inArray(cardNumber,play),1);
                    $(this).css("background-color","#00F7DE")
                }
            }
        });
    }

//声明牌
    $("#stateDiv span").click(function () {
        $("#stateDiv span").css("background-color","");
        $(this).css("background-color","#ffff00");
        state = $(this).attr("number");
    })

// 出牌
    $("#takeOutBtn").click(function () {
        // 0出牌, 1揭牌, 2过
        // username=小虎&operation=出牌&candidateArray=1,2,3,4&state=4
        if (play.length == 0){
            alert("请选择要出的牌");
            return ;
        }
        if (response.state == null && state == null){
            alert("先生押的是几呢?")
            return ;
        }
        var message = "username=" + username + "&operateType=0" + "&play=" + play
            + "&state=" + state + "&grid=" + response.id;
        ws.send(message);

        play = [];
        state = null;
    });

// 揭牌
    $("#exposeBtn").click(function () {
        var message = "username=" + username + "&operateType=1"
            + "&grid=" + response.id;
        ws.send(message);

        play = [];
    });

// 过
    $("#passBtn").click(function () {
        var message = "username=" + username + "&operateType=2"
            + "&grid=" + response.id;
        ws.send(message);

        play = [];
    });

// 给纸牌打上花色
    function flower(cardNumber) {
        if (cardNumber - 39 > 0){
            return (cardNumber - 39)+"<br/>方片";
        }
        if (cardNumber - 26 > 0){
            return (cardNumber - 26)+"<br/>梅花";
        }
        if (cardNumber - 13 > 0){
            return (cardNumber - 13)+"<br/>红桃";
        }
        return cardNumber + "<br/>黑桃";
    }

    // 游戏结束
    function over() {
        $("#startNumber").show();
        $("#matching").show();
        $("#schedule").html("");
        $("#desk").html("");
        $("#hand").html("");
        $("#takeOutBtn, #stateDiv, #exposeBtn, #passBtn, #wait").hide();
    }


});
