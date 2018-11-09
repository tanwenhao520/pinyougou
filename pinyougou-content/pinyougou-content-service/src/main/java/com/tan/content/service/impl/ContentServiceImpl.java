package com.tan.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tan.content.service.ContentService;
import com.tan.dao.ContentMapper;
import com.tan.pojo.TbContent;
import com.tan.service.impl.BaseServiceImpl;
import com.tan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    private static final String REDIS_CONTENT = "content";
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 重写父类add方法 管理员进行增加时调用父类的添加方法进行添加，之后再删除Redis里面的缓存，
     * 一旦用户进行查看首页时，缓存没有广告信息就会去调用findContentListByCategoryId(id)方法进行添加缓存
     * @param tbContent
     */
    @Override
    public void add(TbContent tbContent){

        super.add(tbContent);
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     *管理员进行修改广告内容时，如果管理员修改了分区则删除全部的Redis缓存，如果没有则只删除之前的缓存
     * @param tbContent
     */
    @Override
    public void update(TbContent tbContent){

        TbContent oldTbContent = findOne(tbContent.getId());

        super.update(tbContent);

        if(!oldTbContent.getCategoryId().equals(tbContent.getCategoryId())){
            updateContentInRedisByCategoryId(oldTbContent.getCategoryId());
        }
        updateContentInRedisByCategoryId(tbContent.getCategoryId());

    }


    /**
     *管理原进行了删除广告时，先查询管理员是否删除了内容如果删除则把Redis缓存删除
     * @param ids
     */
    @Override
    public void deleteByIds(Serializable[] ids){

        Example example = new Example(TbContent.class);

        example.createCriteria().andIn("id", Arrays.asList(ids));

        List<TbContent> tbContents = contentMapper.selectByExample(example);
        if(tbContents.size() > 0 && tbContents != null){
            for (TbContent tbContent : tbContents) {
                updateContentInRedisByCategoryId(tbContent.getCategoryId());
            }
        }
        super.deleteByIds(ids);

    }
    /**
     * 删除Redis缓存
     * @param categoryId
     */
    private void updateContentInRedisByCategoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询广告  先判断是否有Redis缓存，如果有返回缓存里面的数据，如果没有则去Mysql查询，在放到Redis里面去
     * @param id
     * @return
     */
    @Override
    public List<TbContent> findContentListByCategoryId(Long id) {

        List<TbContent> list = null;

        try {
            list  = (List<TbContent>)redisTemplate.boundHashOps(REDIS_CONTENT).get(id);
            if(list != null){
                return  list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Example example = new Example(TbContent.class);

        example.createCriteria().andEqualTo("status","1").andEqualTo("categoryId",id);

        example.orderBy("sortOrder").desc();

        list = contentMapper.selectByExample(example);

        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).put(id,list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  list;
    }
}
