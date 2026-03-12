<template>
  <div>
    <el-card style="margin: 18px 2%;width: 95%">
      <div slot="header" class="clearfix">
        <span>用户书籍收藏管理</span>
        <el-tag type="info" style="margin-left: 10px">共 {{ collections.length }} 条记录</el-tag>
      </div>
      <el-table
        v-loading="loading"
        :data="collections.slice((currentPage-1)*pageSize, currentPage*pageSize)"
        border
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="80">
        </el-table-column>
        <el-table-column
          label="用户"
          width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.user.username }}</span>
            <el-tag size="mini" style="margin-left: 5px">{{ scope.row.user.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="书籍"
          min-width="200">
          <template slot-scope="scope">
            <el-avatar :src="scope.row.book.cover" size="small" style="margin-right: 10px"></el-avatar>
            <span>{{ scope.row.book.title }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="book.author"
          label="作者"
          width="120">
        </el-table-column>
        <el-table-column
          prop="collectTime"
          label="收藏时间"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              @click="removeCollection(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="collections.length"
        layout="total, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: right">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserBookCollection',
  data () {
    return {
      collections: [],
      loading: false,
      currentPage: 1,
      pageSize: 20
    }
  },
  mounted () {
    this.loadAllCollections()
  },
  methods: {
    loadAllCollections () {
      this.loading = true
      this.$axios.get('/admin/collection/all').then(resp => {
        if (resp && resp.data.code === 200) {
          this.collections = resp.data.result
        } else if (resp.data.code === 401) {
          this.$message.error('请先登录')
          this.$router.push('/login')
        } else {
          this.$message.error(resp.data.message || '加载失败')
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
        this.$message.error('加载失败')
      })
    },
    removeCollection (row) {
      this.$confirm(`确定要删除用户 [${row.user.username}] 对书籍 [${row.book.title}] 的收藏吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/collection/remove/' + row.book.id).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('删除成功')
            this.loadAllCollections()
          } else {
            this.$message.error(resp.data.message || '删除失败')
          }
        })
      })
    },
    handleCurrentChange (currentPage) {
      this.currentPage = currentPage
    }
  }
}
</script>

<style scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
</style>
