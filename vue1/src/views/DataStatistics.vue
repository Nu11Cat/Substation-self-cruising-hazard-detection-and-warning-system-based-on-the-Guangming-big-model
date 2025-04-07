<template>
  <div class="statistics-container">
    <h2 class="data-header">数据分析</h2>
    
    <!-- 数据统计卡片 -->
    <div class="statistics-cards">
      <div class="data-card">
        <div class="title">本月处理总数</div>
        <div class="content">{{ statistics.mouth || 0 }}</div>
      </div>
      <div class="data-card">
        <div class="title">今日处理数</div>
        <div class="content">{{ statistics.today || 0 }}</div>
      </div>
      <div class="data-card">
        <div class="title">待处理数</div>
        <div class="content">{{ statistics.week || 0 }}</div>
      </div>
    </div>

    <!-- 标记分类统计 -->
    <div class="chart-section">
      <div class="chart-header">
        <div class="chart-title">分类统计</div>
      </div>
      <div class="chart-container" ref="markingChart"></div>
    </div>

    <!-- 处理统计 -->
    <div class="processing-charts">
      <div class="chart-section">
        <div class="chart-header">
          <div class="chart-title">处理趋势</div>
          <el-date-picker
            v-model="dateRange"
            class="date-picker"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :picker-options="pickerOptions"
            @change="handleDateChange">
          </el-date-picker>
        </div>
        <div class="chart-container" ref="lineChart"></div>
      </div>

      <div class="chart-section">
        <div class="chart-header">
          <div class="chart-title">处理数量统计</div>
        </div>
        <div class="chart-container" ref="barChart"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataStatistics',
  data() {
    return {
      statistics: {
        today: 0,
        week: 0,
        mouth: 0,
        rows: []
      },
      dateRange: [],
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 1)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setMonth(start.getMonth() - 3)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      charts: {
        marking: null,
        line: null,
        bar: null
      },
      markingMap: {
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
  mounted() {
    this.initCharts()
    this.updateCharts()
  },
  methods: {
    initCharts() {
      // 初始化标记分类统计图表
      this.charts.marking = this.$echarts.init(this.$refs.markingChart)
      this.charts.marking.setOption({
        title: {
          text: '标记分布',
          left: 'center',
          top: 20,
          textStyle: {
            fontSize: 16,
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}条 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 20,
          top: 'center',
          itemWidth: 25,
          itemHeight: 14,
          textStyle: {
            fontSize: 14
          }
        },
        series: [{
          name: '标记类型',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: []
        }]
      })

      // 初始化处理趋势图表
      this.charts.line = this.$echarts.init(this.$refs.lineChart)
      this.charts.line.setOption({
        title: {
          text: '处理趋势分析',
          left: 'center',
          top: 20,
          textStyle: {
            fontSize: 16,
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: [],
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          name: '处理数量',
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3,
            shadowColor: 'rgba(0,0,0,0.2)',
            shadowBlur: 10
          },
          showSymbol: true,
          symbol: 'circle',
          symbolSize: 8,
          areaStyle: {
            opacity: 0.3,
            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: '#409EFF'
            }, {
              offset: 1,
              color: 'rgba(64,158,255,0.1)'
            }])
          },
          data: []
        }]
      })

      // 初始化处理数量统计图表
      this.charts.bar = this.$echarts.init(this.$refs.barChart)
      this.charts.bar.setOption({
        title: {
          text: '处理数量分布',
          left: 'center',
          top: 20,
          textStyle: {
            fontSize: 16,
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1
        },
        series: [{
          name: '处理数量',
          type: 'bar',
          barWidth: '60%',
          itemStyle: {
            borderRadius: [4, 4, 0, 0],
            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: '#409EFF'
            }, {
              offset: 1,
              color: '#53a8ff'
            }])
          },
          data: []
        }]
      })

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        chart && chart.resize()
      })
    },
    updateCharts() {
      // 更新标记分类统计
      if (this.statistics.rows && this.statistics.rows.length > 0) {
        const markingData = this.processMarkingData(this.statistics.rows)
        this.charts.marking.setOption({
          series: [{
            data: markingData
          }]
        }, true)
      }

      // 更新处理趋势和数量统计
      if (this.statistics.rows && this.statistics.rows.length > 0) {
        const { dates, counts } = this.processTimeData(this.statistics.rows)
        
        this.charts.line.setOption({
          xAxis: {
            data: dates
          },
          series: [{
            data: counts
          }]
        }, true)

        this.charts.bar.setOption({
          xAxis: {
            data: dates
          },
          series: [{
            data: counts
          }]
        }, true)
      }
    },
    processMarkingData(images) {
      const markingMap = {}
      images.forEach(image => {
        if (image.markings) {
          // 直接获取标记名称并转换为中文
          const tag = image.markings.split(' with')[0].trim()
          const chineseName = this.markingMap[tag]
          if (chineseName) {
            markingMap[chineseName] = (markingMap[chineseName] || 0) + 1
          }
        }
      })
      return Object.entries(markingMap).map(([name, value]) => ({
        name,
        value
      }))
    },
    processTimeData(images) {
      // 处理时间数据的逻辑保持不变
      const dateMap = {}
      images.forEach(image => {
        const date = image.createTime.split(' ')[0]
        dateMap[date] = (dateMap[date] || 0) + 1
      })
      const sortedDates = Object.keys(dateMap).sort()
      return {
        dates: sortedDates,
        counts: sortedDates.map(date => dateMap[date])
      }
    },
    handleDateChange() {
      this.updateCharts()
    }
  },
  beforeDestroy() {
    // 清理事件监听
    window.removeEventListener('resize', this.handleResize)
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      chart && chart.dispose()
    })
  }
}
</script>

<style>
  /* Add your styles here */
  .statistics-container {
    padding: 20px;
    background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
    min-height: calc(100vh - 100px);
    margin: -20px;
  }

  .data-header {
    margin-bottom: 24px;
    font-size: 24px;
    font-weight: 600;
    color: #333;
    position: relative;
    padding-left: 14px;
  }

  .data-header::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 24px;
    background: linear-gradient(to bottom, #409EFF, #53a8ff);
    border-radius: 2px;
  }

  .statistics-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
  }

  .data-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    cursor: pointer;
  }

  .data-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  }

  .data-card .title {
    font-size: 15px;
    color: #606266;
    margin-bottom: 12px;
  }

  .data-card .content {
    font-size: 28px;
    font-weight: 600;
    background: linear-gradient(45deg, #409EFF, #53a8ff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .chart-section {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  }

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .chart-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    position: relative;
    padding-left: 12px;
  }

  .chart-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 18px;
    background: linear-gradient(to bottom, #409EFF, #53a8ff);
    border-radius: 2px;
  }

  .chart-container {
    height: 400px;
    width: 100%;
  }

  .processing-charts {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 24px;
  }

  @media screen and (max-width: 1200px) {
    .processing-charts {
      grid-template-columns: 1fr;
    }
  }

  @media screen and (max-width: 768px) {
    .statistics-cards {
      grid-template-columns: 1fr;
    }
    
    .chart-section {
      padding: 16px;
    }
    
    .chart-container {
      height: 300px;
    }
  }

  /* ECharts 图表样式优化 */
  :deep(.echarts) {
    border-radius: 8px;
    background: #fff;
  }

  /* 日期选择器样式 */
  .date-picker {
    :deep(.el-input__inner) {
      height: 32px;
      line-height: 32px;
      font-size: 14px;
    }
    
    :deep(.el-range-separator) {
      line-height: 24px;
    }
  }

  .charts-row,
  .half-width {
    display: none;
  }
</style> 