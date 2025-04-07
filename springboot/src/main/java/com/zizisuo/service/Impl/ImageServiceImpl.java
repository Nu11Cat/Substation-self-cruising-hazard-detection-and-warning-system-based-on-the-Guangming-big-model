package com.zizisuo.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zizisuo.controller.UploadController;
import com.zizisuo.mapper.ImageMapper;
import com.zizisuo.pojo.*;
import com.zizisuo.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private UploadController uploadController;

    @Override
    public PageBean page(Integer page, Integer pageSize, String name, String markings, String createUser, LocalDate begin, LocalDate end) {
        // 分页查询
        PageHelper.startPage(page, pageSize);

        List<Image> list = imageMapper.list(name, markings, createUser, begin, end);
        Page<Image> p = (Page<Image>) list;

        PageBean pageBean = new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    /**
     * 删除数据
     * @param id
     */
    @Override
    public void delete(Integer id) {
        imageMapper.delete(id);
    }

    /**
     * 新增数据
     * @param image
     */
    @Override
    public void add(Image image) {
        image.setCreateTime(LocalDateTime.now());
        image.setUpdateTime(LocalDateTime.now());
        image.setCreateUser(image.getCreateUser());
        image.setUpdateUser(image.getUpdateUser());
        imageMapper.add(image);
    }

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @Override
    public Image getById(Integer id) {
        return imageMapper.getById(id);
    }

    /**
     * 更新数据
     * @param image
     */
    @Override
    public void update(Image image) {
        image.setUpdateTime(LocalDateTime.now());
        image.setUpdateUser(image.getUpdateUser());
        imageMapper.update(image);
    }

    /**
     * 处理后数据
     * @param perUrl
     * @return
     */
    @Override
    public PostMessage topost(String perUrl) {
        PostMessage postMessage = new PostMessage();

        try {
            // 1. 下载图片
            URL url = new URL(perUrl);
            File imageFile = File.createTempFile("temp_image", ".jpg");
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("Image downloaded: " + imageFile.getAbsolutePath());

            // 2. 发送到 Flask 处理
            String flaskUrl = "http://localhost:5000/process_image";
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(flaskUrl);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody("file", imageFile, org.apache.http.entity.ContentType.DEFAULT_BINARY, imageFile.getName());
                httpPost.setEntity(builder.build());

                System.out.println("Sending image to Flask...");
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) {
                        // 3. 读取 `messages`（从响应头获取）
                        String resultMessages = response.getFirstHeader("messages").getValue();
                        System.out.println("Detected Objects: " + resultMessages);

                        // 4. 读取 Flask 返回的图片（字节流）
                        byte[] responseBytes = EntityUtils.toByteArray(responseEntity);

                        // 5. 存为临时图片
                        File resultImageFile = File.createTempFile("result_image", ".jpg");
                        try (FileOutputStream out = new FileOutputStream(resultImageFile)) {
                            out.write(responseBytes);
                        }
                        System.out.println("Processed image saved: " + resultImageFile.getAbsolutePath());

                        // 6. 上传到 Java 服务器，获取图片 URL
                        MultipartFile multipartFile = new MockMultipartFile(
                                "file",
                                resultImageFile.getName(),
                                "image/jpeg",
                                new FileInputStream(resultImageFile)
                        );
                        Result result = uploadController.upload(multipartFile);
                        String resultImageUrl = (String) result.getData();

                        // 7. 设置返回值
                        postMessage.setPostUrl(resultImageUrl);
                        postMessage.setMessages(resultMessages);
                    } else {
                        throw new IOException("Failed to get response from Flask");
                    }
                }
            }

            // 删除临时文件
            imageFile.delete();
            System.out.println("Temporary files deleted.");
        } catch (IOException e) {
            e.printStackTrace();
            postMessage.setPostUrl("Error: " + e.getMessage());
            postMessage.setMessages("Error: " + e.getMessage());
        }

        return postMessage;
    }



    /**
     * 获取统计数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public DataBean getdata(LocalDate begin, LocalDate end) {
        int data1 = imageMapper.getDataDay();
        int data2 = imageMapper.getDataWeek();
        int data3 = imageMapper.getDataMouth();
        List<Image> list = imageMapper.getDataList(begin, end);

        DataBean data = new DataBean(data1, data2, data3, list);
        return data;
    }
}