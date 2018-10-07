package com.group.repository.common;

import com.group.dto.PageModel;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseRepositoryImpl<T,ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T,ID> {


    private final EntityManager entityManager;

    // 父类没有不带参数的构造方法，这里手动构造父类
    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    // 通用执行sql以及时间格式处理
    private List<Map<String, Object>> toListMap(String sql){
        Query nativeQuery = entityManager.createNativeQuery(sql);
        List<Map<String, Object>> list = nativeQuery.unwrap(SQLQuery.class).setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
        list.forEach(e -> {
            if (e.containsKey("createtime")) {
                String createtime = e.get("createtime").toString();
                e.put("createtime", createtime.substring(0, createtime.length()-2));
            }
        });
        entityManager.close();
        return list;
    };

    // 结果集
    public List<Map<String, Object>> listBySQL(String sql) {
        return toListMap(sql);
    }

    // 只取第一行结果
    public Map<String, Object> mapBySQL(String sql) {
        return toListMap(sql).get(0);
    }

    // 分页
    public PageModel<Map<String, Object>> LeafBySQL(Integer currentPage, Integer pageSize, String sql) {
        PageModel<Map<String, Object>> pageModel = new PageModel<>();
        if (currentPage == null){ currentPage = 1; }
        if (pageSize == null){ pageSize = 10; }

        // 总条数
        String sqlCount = "select count(0) count_num from (" + sql + ") as total";
        Integer totalCount = Integer.parseInt(toListMap(sqlCount).get(0).get("count_num").toString());
        pageModel.setTotalCount(totalCount);
        // 总页数
        pageModel.setTotalPage(totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1);
        // 数据
        String sqlData = sql + " limit "+(currentPage-1) * pageSize+" , "+pageSize;
        pageModel.setList(toListMap(sqlData));
        // 其他
        pageModel.setCurrentPage(currentPage);
        pageModel.setPageSize(pageSize);

        return pageModel;
    }

}