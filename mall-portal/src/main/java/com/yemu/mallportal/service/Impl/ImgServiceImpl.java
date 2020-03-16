package com.yemu.mallportal.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yemu.mallportal.entity.Img;
import com.yemu.mallportal.mapper.ImgMapper;
import com.yemu.mallportal.service.ImgService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImgServiceImpl extends ServiceImpl<ImgMapper, Img> implements ImgService {
    @Override
    public List<Img> getByPid(int pid) {
        return baseMapper.getByPid(pid);
    }
}