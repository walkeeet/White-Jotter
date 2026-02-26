<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-card style="margin: 18px 2%;width: 95%">
      <div slot="header">
        <span>我的收藏书籍</span>
      </div>
      <el-table
        :data="favoriteBooks"
        stripe
        style="width: 100%"
        :max-height="tableHeight"
        v-loading="loading">
        <el-table-column
          label="封面"
          width="120">
          <template slot-scope="scope">
            <img :src="scope.row.cover" alt="封面" style="width: 80px; height: 120px; object-fit: cover;">
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          label="书名"
          fit>
        </el-table-column>
        <el-table-column
          prop="author"
          label="作者"
          width="150">
        </el-table-column>
        <el-table-column
          prop="date"
          label="出版日期"
          width="120">
        </el-table-column>
        <el-table-column
          prop="press"
          label="出版社"
          width="150">
        </el-table-column>
        <el-table-column
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              @click="removeFavorite(scope.row.id)"
              type="danger"
              size="small"
              icon="el-icon-delete">
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="favoriteBooks.length === 0 && !loading" style="text-align: center; padding: 50px; color: #909399;">
        <i class="el-icon-star-off" style="font-size: 50px;"></i>
        <p>暂无收藏的书籍</p>
        <el-button type="primary" @click="goToLibrary">去图书馆逛逛</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'FavoriteBooks',
    data () {
      return {
        favoriteBooks: [],
        loading: false
      }
    },
    mounted () {
      this.loadFavoriteBooks()
    },
    computed: {
      tableHeight () {
        return window.innerHeight - 320
      }
    },
    methods: {
      loadFavoriteBooks () {
        var _this = this
        _this.loading = true
        this.$axios.get('/book/favorites').then(resp => {
          _this.loading = false
          if (resp && resp.data.code === 200) {
            _this.favoriteBooks = resp.data.result
          } else {
            _this.$message.error(resp.data.message || '获取收藏列表失败')
          }
        }).catch(() => {
          _this.loading = false
          _this.$message.error('获取收藏列表失败')
        })
      },
      removeFavorite (bid) {
        var _this = this
        this.$confirm('确定要取消收藏该书籍吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.$axios.post('/book/unfavorite', { bid: bid }).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('取消收藏成功')
              _this.favoriteBooks = _this.favoriteBooks.filter(book => book.id !== bid)
            } else {
              _this.$message.error(resp.data.message || '取消收藏失败')
            }
          })
        }).catch(() => {})
      },
      goToLibrary () {
        this.$router.push('/library')
      }
    }
  }
</script>

<style scoped>
</style>
