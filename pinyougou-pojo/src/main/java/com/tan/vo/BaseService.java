package com.tan.vo;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {
    //查询一条记录
    public T findOne(Serializable id);
    //查询所有记录
    public List<T> findAll();
    //按条件查询
    public List<T> findByWhere(T t);
    //查询并分页
    public PageResult findPage(Integer page,Integer rows);
    //按照条件查询并分页
    public PageResult findPage(Integer page,Integer rows,T t);
    //新增
    public void add(T t);
    //根据主键更新
    public void update(T t);
    //批量删除
    public void deleteById(Serializable[] ids);
}
