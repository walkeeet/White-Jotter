<template>
  <div>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header"><span>我的收藏</span></div>
          <el-table :data="collects" style="width: 100%">
            <el-table-column prop="book.id" label="书籍ID" width="100">
            </el-table-column>
            <el-table-column prop="book.title" label="书名">
            </el-table-column>
            <el-table-column prop="book.author" label="作者" width="150">
            </el-table-column>
            <el-table-column prop="collectTime" label="收藏时间" width="200" :formatter="formatDate">
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template slot-scope="scope">
                <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="cancelCollect(scope.row.book.id)"></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  export default {
    name: 'CollectManagement',
    data () {
      return {
        collects: []
      }
    },
    mounted: function () {
      this.loadCollects()
    },
    methods: {
      loadCollects () {
        var _this = this
        this.$axios.get('/api/collect').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.collects = resp.data.result
          }
        })
      },
      cancelCollect (bookId) {
        var _this = this
        this.$axios.delete('/api/collect/' + bookId).then(resp => {
          if (resp.data.code === 200) {
            _this.$message.success('取消收藏成功')
            _this.loadCollects()
          }
        })
      },
      formatDate (row, column) {
        var date = new Date(row.collectTime)
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        })
      }
    }
  }
</script>

<style scoped>

</style>
