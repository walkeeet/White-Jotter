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
      <div v-if="favorites.length === 0" class="empty-text">
        暂无收藏书籍
      </div>
      <el-row v-else>
        <el-tooltip effect="dark" placement="right"
                    v-for="item in favorites.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                    :key="item.id">
          <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.book.title}}</p>
          <p slot="content" style="font-size: 13px;margin-bottom: 6px">
            <span>{{item.book.author}}</span> /
            <span>{{item.book.date}}</span> /
            <span>{{item.book.press}}</span>
          </p>
          <p slot="content" style="width: 300px" class="abstract">{{item.book.abs}}</p>
          <el-card style="width: 135px;margin-bottom: 20px;height: 253px;float: left;margin-right: 15px" class="book"
                   bodyStyle="padding:10px" shadow="hover">
            <div class="cover">
              <img :src="item.book.cover" alt="封面">
            </div>
            <div class="info">
              <div class="title">
                <a href="">{{item.book.title}}</a>
              </div>
            </div>
            <div class="author">{{item.book.author}}</div>
            <div class="favorite-btn">
              <el-button
                type="danger"
                icon="el-icon-star-on"
                size="mini"
                circle
                @click.stop="removeFavorite(item)"></el-button>
            </div>
          </el-card>
        </el-tooltip>
      </el-row>
      <el-row v-if="favorites.length > 0">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          :total="favorites.length">
        </el-pagination>
      </el-row>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'UserFavorites',
    data () {
      return {
        favorites: [],
        currentPage: 1,
        pagesize: 18
      }
    },
    mounted: function () {
      this.loadFavorites()
    },
    methods: {
      loadFavorites () {
        var _this = this
        this.$axios.get('/favorites').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.favorites = resp.data.result
          } else {
            _this.$message.error('获取收藏列表失败')
          }
        }).catch(err => {
          if (err.response && err.response.status === 401) {
            _this.$message.warning('请先登录')
            _this.$router.push('/login')
          } else {
            _this.$message.error('获取收藏列表失败')
          }
        })
      },
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage
      },
      removeFavorite (item) {
        var _this = this
        this.$confirm('确定取消收藏该书籍吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.$axios.delete('/favorites/' + item.bookId).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('取消收藏成功')
              _this.loadFavorites()
            } else {
              _this.$message.error(resp.data.message || '取消收藏失败')
            }
          }).catch(() => {
            _this.$message.error('取消收藏失败')
          })
        }).catch(() => {
        })
      }
    }
  }
</script>

<style scoped>
  .cover {
    width: 115px;
    height: 172px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
  }

  img {
    width: 115px;
    height: 172px;
  }

  .title {
    font-size: 14px;
    text-align: left;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    margin-bottom: 6px;
    text-align: left;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .favorite-btn {
    text-align: center;
    margin-top: 5px;
  }

  .empty-text {
    text-align: center;
    color: #909399;
    padding: 40px 0;
    font-size: 14px;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
</style>
