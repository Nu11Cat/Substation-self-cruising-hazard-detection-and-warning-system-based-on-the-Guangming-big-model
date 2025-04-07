<template>
  <div class="image-detail">
    <el-card>
      <div slot="header">
        <span>图片详情</span>
        <el-button style="float: right" @click="$router.push('/list')">返回列表</el-button>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="创建用户">{{ detail.createUser }}</el-descriptions-item>
        <el-descriptions-item label="更新用户">{{ detail.updateUser }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="标记">{{ detail.markings }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.other }}</el-descriptions-item>
      </el-descriptions>

      <div class="image-container">
        <div class="image-box">
          <h3>处理前图片</h3>
          <el-image
            style="width: 400px; height: 400px"
            :src="detail.perUrl"
            :preview-src-list="[detail.perUrl]">
          </el-image>
        </div>
        <div class="image-box">
          <h3>处理后图片</h3>
          <el-image
            style="width: 400px; height: 400px"
            :src="detail.postUrl"
            :preview-src-list="[detail.postUrl]">
          </el-image>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ImageDetail',
  data() {
    return {
      detail: {}
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      try {
        const response = await axios.get(`/images/${this.$route.params.id}`)
        if (response.data.code === 1) {
          this.detail = response.data.data
        } else {
          this.$message.error(response.data.msg || '获取详情失败')
        }
      } catch (error) {
        console.error('获取详情失败:', error)
        this.$message.error('获取详情失败')
      }
    }
  }
}
</script>

<style scoped>
.image-detail {
  padding: 20px;
}
.image-container {
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
}
.image-box {
  text-align: center;
}
.image-box h3 {
  margin-bottom: 10px;
}
</style> 