<template>
  <div>
    <div class="detection-container">
      <!-- 中间图片对比区域 -->
      <div class="image-comparison">
        <div class="image-comparison-top">
          <div class="image-box">
            <h3>处理前图片</h3>
            <div class="image-wrapper" @click="handleSelectImage">
              <img v-if="form.perUrl" :src="form.perUrl" class="preview-image">
              <div v-else class="upload-placeholder">
                <i class="el-icon-plus"></i>
                <div class="upload-text">点击上传图片</div>
              </div>
            </div>
            <div class="button-group">
              <el-button type="primary" size="small" @click="handleSelectImage">选择图片</el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="processImage" 
                :disabled="!form.perUrl || processing">
                {{ processing ? '处理中...' : '开始处理' }}
              </el-button>
            </div>
          </div>
          <div class="image-box">
            <h3>处理后图片</h3>
            <div class="image-wrapper">
              <img v-if="form.postUrl" :src="form.postUrl" class="preview-image">
              <div v-else class="placeholder-image">
                <span>等待处理结果</span>
              </div>
            </div>
            <el-button type="success" size="small" @click="handleSaveResult" :disabled="!form.postUrl">保存结果</el-button>
          </div>
        </div>
        <div class="image-comparison-bottom">
          <div class="detection-types">
            <h3>检测类型说明</h3>
            <div class="types-grid">
              <div class="type-category">
                <h4>表计相关</h4>
                <ul>
                  <li>表盘模糊</li>
                  <li>表盘破损</li>
                  <li>外壳破损</li>
                  <li>表计读数有错</li>
                </ul>
              </div>
              <div class="type-category">
                <h4>设备状态</h4>
                <ul>
                  <li>绝缘子破裂</li>
                  <li>硅胶变色</li>
                  <li>硅胶体破损</li>
                  <li>箱门闭合异常</li>
                  <li>盖板破损</li>
                </ul>
              </div>
              <div class="type-category">
                <h4>环境异常</h4>
                <ul>
                  <li>地面油污</li>
                  <li>挂空悬浮物</li>
                  <li>鸟巢</li>
                </ul>
              </div>
              <div class="type-category">
                <h4>人员违规</h4>
                <ul>
                  <li>未穿安全帽</li>
                  <li>未穿工装</li>
                  <li>吸烟</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧检测消息 -->
      <div class="detection-messages">
        <div class="messages-header">
          <span>检测结果</span>
          <el-button type="text" @click="clearMessages">清空</el-button>
        </div>
        <div class="messages-content">
          <div v-if="messages.length === 0" class="no-messages">
            暂无检测结果
          </div>
          <template v-else>
            <div v-for="(message, index) in messages" :key="index" class="message-item">
              <i class="el-icon-warning-outline"></i>
              <div class="message-content">
                <span class="message-type">{{ message.type }}</span>
                <span class="message-confidence">置信度：{{ message.confidence }}</span>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 保存表单对话框 -->
    <el-dialog title="保存检测结果" :visible.sync="saveDialogVisible" width="500px">
      <el-form :model="saveForm" ref="saveForm" label-width="80px">
        <el-form-item label="名称" prop="name" :rules="[
          { required: true, message: '请输入名称', trigger: 'blur' }
        ]">
          <el-input v-model="saveForm.name" placeholder="请输入名称"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="other">
          <el-input type="textarea" v-model="saveForm.other" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="saveDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitSave">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { formatDate } from '@/utils/date'

export default {
  name: 'ImageDetection',
  data() {
    return {
      form: {
        perUrl: '',
        postUrl: '',
        name: '',
        markings: '',
        other: ''
      },
      messages: [],
      processing: false,
      saveDialogVisible: false,
      saveForm: {
        name: '',
        other: ''
      },
      messageMap: {
        'bj_bpmh': '表盘模糊',
        'bj_bpps': '表盘破损',
        'bj_wkps': '外壳破损',
        'bjdsyc': '表计读数有错',
        'jyz_pl': '绝缘子破裂',
        'sly_dmyw': '地面油污',
        'hxq_gjbs': '硅胶变色',
        'hxq_gjtps': '硅胶体破损',
        'xmbhyc': '箱门闭合异常',
        'yw_gkxfw': '挂空悬浮物',
        'yw_nc': '鸟巢',
        'gbps': '盖板破损',
        'wcaqm': '未穿安全帽',
        'wcgz': '未穿工装',
        'xy': '吸烟'
      }
    }
  },
  methods: {
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.form.perUrl = response.data
        this.$message.success('上传成功')
      } else {
        this.$message.error('上传失败: ' + response.msg)
      }
    },
    handleUploadError(err) {
      console.error('上传失败:', err)
      this.$message.error('上传失败: ' + (err.message || '未知错误'))
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    },
    handleSelectImage() {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = 'image/*'
      input.onchange = (e) => {
        const file = e.target.files[0]
        if (this.beforeUpload(file)) {
          this.uploadFile(file)
        }
      }
      input.click()
    },
    async uploadFile(file) {
      try {
        const formData = new FormData()
        formData.append('image', file)
        
        const response = await axios.post('http://localhost:8080/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        this.handleUploadSuccess(response.data)
      } catch (error) {
        this.handleUploadError(error)
      }
    },
    async processImage() {
      if (!this.form.perUrl || this.processing) return

      this.processing = true
      try {
        const response = await axios.get(`http://localhost:8080/images/post`, {
          params: {
            perUrl: this.form.perUrl
          }
        })
        if (response.data.code === 1) {
          this.form.postUrl = response.data.data.postUrl
          // 处理返回的messages
          if (response.data.data.messages) {
            this.messages = response.data.data.messages.split(',')
              .map(msg => {
                const parts = msg.trim().split(' with confidence ')
                if (parts.length !== 2) return null
                
                const [type, confidence] = parts
                if (!this.messageMap[type]) return null
                
                return {
                  type: this.messageMap[type],
                  confidence: parseFloat(confidence).toFixed(2)
                }
              })
              .filter(item => item !== null) // 过滤掉不在映射表中的项
          }
          this.$message.success('处理完成')
        } else {
          this.$message.error('处理失败: ' + response.data.msg)
        }
      } catch (error) {
        this.$message.error('处理失败: ' + error.message)
      } finally {
        this.processing = false
      }
    },
    handleSaveResult() {
      this.saveForm.name = '检测结果_' + formatDate(new Date(), 'yyyy-MM-dd_HH:mm:ss')
      this.saveForm.other = ''
      this.saveDialogVisible = true
    },
    async submitSave() {
      try {
        await this.$refs.saveForm.validate()
        
        const data = {
          ...this.form,
          name: this.saveForm.name,
          other: this.saveForm.other,
          createUser: 'admin',
          updateUser: 'admin',
          markings: this.messages.map(msg => msg.type).join(',')
        }

        const response = await axios.post('http://localhost:8080/images', data)
        if (response.data.code === 1) {
          this.$message.success('保存成功')
          this.saveDialogVisible = false
        } else {
          this.$message.error('保存失败: ' + response.data.msg)
        }
      } catch (error) {
        if (error.message) {
          this.$message.error('保存失败: ' + error.message)
        }
      }
    },
    clearMessages() {
      this.messages = []
    }
  }
}
</script>

<style scoped>
.detection-container {
  display: flex;
  height: calc(100vh - 140px);
  gap: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
  border-radius: 12px;
  margin: -20px;
}

.image-comparison {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: 100%;
}

.image-comparison-top {
  height: 70%;
  display: flex;
  gap: 24px;
}

.image-comparison-bottom {
  height: 30%;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  overflow: auto;
}

.image-comparison-bottom:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.image-box {
  flex: 1;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.image-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.image-box h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  text-align: center;
  position: relative;
  padding-bottom: 12px;
}

.image-box h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #409EFF, #53a8ff);
  border-radius: 3px;
}

.button-group {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}

/* 添加自定义按钮样式 */
.image-box .el-button--small {
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.image-box .el-button--small:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-box .el-button--primary {
  background: linear-gradient(135deg, #409EFF, #53a8ff);
  border: none;
}

.image-box .el-button--warning {
  background: linear-gradient(135deg, #e6a23c, #f3b761);
  border: none;
}

.image-box .el-button--success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  border: none;
}

/* 禁用状态的按钮样式 */
.image-box .el-button--small.is-disabled {
  background: #a0cfff;
  border: none;
  opacity: 0.7;
  cursor: not-allowed;
}

.image-box .el-button--warning.is-disabled {
  background: #f3d19e;
  border: none;
  opacity: 0.7;
}

.image-box .el-button--success.is-disabled {
  background: #b3e19d;
  border: none;
  opacity: 0.7;
}

.image-wrapper {
  flex: 1;
  width: 100%;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  overflow: hidden;
  cursor: pointer;
  background: #f8fafc;
  transition: all 0.3s ease;
  position: relative;
}

.image-wrapper:hover {
  border-color: #409EFF;
  background: #f0f7ff;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 4px;
}

.upload-placeholder {
  text-align: center;
  color: #909399;
  padding: 40px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: repeating-linear-gradient(
    45deg,
    #f8fafc,
    #f8fafc 10px,
    #f0f2f5 10px,
    #f0f2f5 20px
  );
}

.upload-placeholder i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #409EFF;
}

.upload-text {
  font-size: 16px;
  color: #606266;
}

.placeholder-image {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 16px;
  background: repeating-linear-gradient(
    45deg,
    #f8fafc,
    #f8fafc 10px,
    #f0f2f5 10px,
    #f0f2f5 20px
  );
}

.detection-messages {
  width: 360px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(64, 158, 255, 0.2);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.detection-messages:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
}

.messages-header {
  padding: 20px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(64, 158, 255, 0.05);
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}

.messages-header span {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.messages-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.no-messages {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
  background: repeating-linear-gradient(
    45deg,
    #f8fafc,
    #f8fafc 10px,
    #f0f2f5 10px,
    #f0f2f5 20px
  );
  border-radius: 8px;
  margin: 10px;
}

.message-item {
  padding: 18px 20px;
  border-radius: 8px;
  background: rgba(64, 158, 255, 0.05);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.message-item:hover {
  background: rgba(64, 158, 255, 0.1);
  transform: translateX(4px);
}

.message-item i {
  color: #409EFF;
  font-size: 22px;
}

.message-item .message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-type {
  color: #303133;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
}

.message-confidence {
  color: #909399;
  font-size: 13px;
  line-height: 1.4;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

/* 自定义滚动条样式 */
.messages-content::-webkit-scrollbar {
  width: 6px;
}

.messages-content::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.messages-content::-webkit-scrollbar-track {
  background: #f8fafc;
}

/* 响应式调整 */
@media screen and (max-width: 1400px) {
  .detection-messages {
    width: 320px;
  }
}

@media screen and (max-width: 1200px) {
  .detection-container {
    flex-direction: column;
    height: auto;
  }
  
  .detection-messages {
    width: 100%;
    height: 300px;
  }
}

/* 修改清空按钮的颜色 */
.messages-header .el-button--text {
  color: #409EFF;
  font-weight: 500;
}

.messages-header .el-button--text:hover {
  color: #66b1ff;
}

.detection-types {
  padding: 12px;
  height: 100%;
}

.detection-types h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  position: relative;
  padding-bottom: 10px;
}

.detection-types h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 2px;
  background: #409EFF;
  border-radius: 2px;
}

.types-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.type-category {
  background: #f8fafc;
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.type-category::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #409EFF;
  opacity: 0.6;
}

.type-category:nth-child(1)::before {
  background: #409EFF;
}

.type-category:nth-child(2)::before {
  background: #67C23A;
}

.type-category:nth-child(3)::before {
  background: #E6A23C;
}

.type-category:nth-child(4)::before {
  background: #F56C6C;
}

.type-category h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.type-category:nth-child(1) h4 {
  color: #409EFF;
}

.type-category:nth-child(2) h4 {
  color: #67C23A;
}

.type-category:nth-child(3) h4 {
  color: #E6A23C;
}

.type-category:nth-child(4) h4 {
  color: #F56C6C;
}

.type-category ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.type-category li {
  padding: 3px 0 3px 8px;
  color: #606266;
  font-size: 13px;
  position: relative;
  transition: all 0.2s ease;
}

.type-category li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.5;
}

.type-category:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 响应式调整 */
@media screen and (max-width: 1400px) {
  .types-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .types-grid {
    grid-template-columns: 1fr;
  }
}
</style> 