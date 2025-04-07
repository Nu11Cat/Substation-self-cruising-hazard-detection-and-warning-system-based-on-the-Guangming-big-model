package com.zizisuo.controller;

import com.zizisuo.pojo.ModelConfig;
import com.zizisuo.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:7070")  // 允许来自7070端口的请求
@RestController
@Slf4j
@RequestMapping("/model")
public class ModelController {

    // 模型文件的基础路径
    private static final String BASE_PATH = "D:/study/zizisuo.project/biandianzhan/all";
    private static final String MODELS_DIR = BASE_PATH + "/py/models";
    private static final String CONFIG_FILE = BASE_PATH + "/py/model_config.properties";
    
    /**
     * 上传模型文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result uploadModel(MultipartFile file) throws IOException {
        log.info("模型文件上传: {}", file.getOriginalFilename());
        
        // 确保目录存在
        File modelDir = new File(MODELS_DIR);
        if (!modelDir.exists()) {
            modelDir.mkdirs();
            log.info("创建目录: {}", modelDir.getAbsolutePath());
        }
        
        // 使用原始文件名保存
        String fileName = file.getOriginalFilename();
        String modelPath = MODELS_DIR + "/" + fileName;
        
        // 保存模型文件
        Path targetPath = Paths.get(modelPath);
        Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        log.info("模型文件已保存: {}", modelPath);
        return Result.success(fileName);
    }
    
    /**
     * 获取所有可用模型列表
     * @return
     */
    @GetMapping("/list")
    public Result getModelList() {
        File modelDir = new File(MODELS_DIR);
        List<String> modelList = new ArrayList<>();
        
        if (modelDir.exists() && modelDir.isDirectory()) {
            modelList = Arrays.stream(modelDir.listFiles())
                    .filter(file -> file.isFile() && file.getName().endsWith(".pt"))
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        
        return Result.success(modelList);
    }
    
    /**
     * 获取当前使用的模型配置
     * @return
     */
    @GetMapping("/current")
    public Result getCurrentModel() {
        ModelConfig config = readModelConfig();
        return Result.success(config);
    }
    
    /**
     * 设置当前使用的模型
     * @param modelName
     * @return
     */
    @PostMapping("/setCurrent")
    public Result setCurrentModel(@RequestParam String modelName) {
        // 先检查文件是否存在
        File modelFile = new File(MODELS_DIR + "/" + modelName);
        if (!modelFile.exists()) {
            return Result.error("模型文件不存在");
        }
        
        // 保存配置
        try {
            saveModelConfig(modelName);
            log.info("当前模型已设置为: {}", modelName);
            return Result.success();
        } catch (IOException e) {
            log.error("保存模型配置失败", e);
            return Result.error("保存模型配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除模型文件
     * @param modelName
     * @return
     */
    @DeleteMapping("/{modelName}")
    public Result deleteModel(@PathVariable String modelName) {
        File modelFile = new File(MODELS_DIR + "/" + modelName);
        if (!modelFile.exists()) {
            return Result.error("模型文件不存在");
        }
        
        // 检查是不是当前使用的模型
        ModelConfig config = readModelConfig();
        if (modelName.equals(config.getCurrentModel())) {
            return Result.error("不能删除当前正在使用的模型");
        }
        
        // 删除模型文件
        if (modelFile.delete()) {
            log.info("模型文件已删除: {}", modelName);
            return Result.success();
        } else {
            return Result.error("删除模型文件失败");
        }
    }
    
    // 读取模型配置
    private ModelConfig readModelConfig() {
        ModelConfig config = new ModelConfig();
        Properties properties = new Properties();
        
        // 确保配置文件存在
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            // 如果配置文件不存在，使用默认值
            config.setCurrentModel("best.pt");
            try {
                saveModelConfig("best.pt");
            } catch (IOException e) {
                log.error("创建默认配置文件失败", e);
            }
            return config;
        }
        
        // 读取配置
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
            config.setCurrentModel(properties.getProperty("current_model", "best.pt"));
        } catch (IOException e) {
            log.error("读取模型配置失败", e);
            config.setCurrentModel("best.pt");
        }
        
        return config;
    }
    
    // 保存模型配置
    private void saveModelConfig(String modelName) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("current_model", modelName);
        
        File configFile = new File(CONFIG_FILE);
        // 确保父目录存在
        configFile.getParentFile().mkdirs();
        
        // 写入配置
        try (FileWriter writer = new FileWriter(configFile)) {
            properties.store(writer, "Model Configuration");
        }
    }
} 