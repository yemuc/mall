package com.yemu.mallportal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yemu.mallportal.entity.Img;
import com.yemu.mallportal.mapper.ImgMapper;
import com.yemu.mallportal.service.ImgService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImgServiceImpl extends ServiceImpl<ImgMapper, Img> implements ImgService {
    @Override
    public List<Img> getMain(int pid) {
        return baseMapper.getMain(pid);
    }

    @Override
    public List<Img> getDetail(int pid) {
        return getBaseMapper().getDetail(pid);
    }
}
