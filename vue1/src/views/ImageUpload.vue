<template>
  <div class="image-upload">
    <el-card>
      <div slot="header">
        <span>图片上传</span>
      </div>

      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"></el-input>
        </el-form-item>

        <el-form-item label="图片" prop="image">
          <el-upload
            class="upload-demo"
            action="http://localhost:8080/update"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :on-error="handleUploadError"
            :show-file-list="false">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件</div>
          </el-upload>
        </el-form-item>

        <el-form-item label="标记" prop="markings">
          <el-input v-model="form.markings" placeholder="请输入标记"></el-input>
        </el-form-item>

        <el-form-item label="备注" prop="other">
          <el-input type="textarea" v-model="form.other" placeholder="请输入备注"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.push('/list')">返回</el-button>
        </el-form-item>
      </el-form>

      <!-- 预览图片 -->
      <div v-if="form.perUrl" class="preview-container">
        <h3>预览图片</h3>
        <el-image
          style="width: 300px; height: 300px"
          :src="form.perUrl"
          :preview-src-list="[form.perUrl]">
        </el-image>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ImageUpload',
  data() {
    return {
      form: {
        name: '',
        perUrl: '',
        postUrl: '',
        markings: '',
        other: '',
        createUser: 'admin',
        updateUser: 'admin'
      },
      rules: {
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' }
        ],
        perUrl: [
          { required: true, message: '请上传图片', trigger: 'change' }
        ],
        markings: [
          { required: true, message: '请输入标记', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.form.perUrl = response.data
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
      }
    },
    handleUploadError() {
      this.$message.error('上传失败')
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    async handleSubmit() {
      try {
        await this.$refs.form.validate()
        const response = await axios.post('/images', this.form)
        if (response.data.code === 1) {
          this.$message.success('提交成功')
          this.$router.push('/list')
        } else {
          this.$message.error(response.data.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.image-upload {
  padding: 20px;
}
.preview-container {
  margin-top: 20px;
  text-align: center;
}
</style> 