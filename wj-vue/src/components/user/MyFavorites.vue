<template>
  <div>
    <nav-menu class="nav-menu"></nav-menu>
    <div class="favorites-container">
      <el-row style="margin: 20px 0 0 20px;">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
        </el-breadcrumb>
      </el-row>
      <el-row style="margin: 20px;">
        <h2 style="text-align: center; margin-bottom: 20px;">我的收藏</h2>
        <div v-if="!isLogin" class="login-tip">
          <p>请先登录后查看收藏</p>
          <el-button type="primary" @click="$router.push('/login')">去登录</el-button>
        </div>
        <div v-else-if="favoriteBooks.length === 0" class="empty-tip">
          <p>您还没有收藏任何书籍</p>
          <el-button type="primary" @click="$router.push('/library')">去图书馆看看</el-button>
        </div>
        <div v-else>
          <el-tooltip effect="dark" placement="right"
                      v-for="item in favoriteBooks.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                      :key="item.id">
            <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
            <p slot="content" style="font-size: 13px;margin-bottom: 6px">
              <span>{{item.author}}</span> /
              <span>{{item.date}}</span> /
              <span>{{item.press}}</span>
            </p>
            <p slot="content" style="width: 300px" class="abstract">{{item.abs}}</p>
            <el-card style="width: 135px;margin-bottom: 20px;height: 250px;float: left;margin-right: 15px" class="book"
                     bodyStyle="padding:10px" shadow="hover">
              <div class="cover">
                <img :src="item.cover" alt="封面">
              </div>
              <div class="info">
                <div class="title">
                  <a href="">{{item.title}}</a>
                </div>
              </div>
              <div class="author">{{item.author}}</div>
              <div class="favorite-btn">
                <el-button
                  type="danger"
                  icon="el-icon-star-on"
                  size="mini"
                  circle
                  @click.stop="cancelFavorite(item.id)">
                </el-button>
              </div>
            </el-card>
          </el-tooltip>
        </div>
      </el-row>
      <el-row v-if="favoriteBooks.length > 0">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          :total="favoriteBooks.length"
          style="text-align: center; margin-top: 20px;">
        </el-pagination>
      </el-row>
    </div>
  </div>
</template>

<script>
  import NavMenu from '@/components/common/NavMenu'

  export default {
    name: 'MyFavorites',
    components: {NavMenu},
    data () {
      return {
        favoriteBooks: [],
        currentPage: 1,
        pagesize: 18
      }
    },
    computed: {
      isLogin () {
        return this.$store.state.username !== ''
      }
    },
    mounted: function () {
      if (this.isLogin) {
        this.loadFavorites()
      }
    },
    methods: {
      loadFavorites () {
        var _this = this
        this.$axios.get('/favorite/list').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.favoriteBooks = resp.data.result
          } else if (resp.data.code === 400) {
            _this.$message.warning(resp.data.message)
          }
        })
      },
      cancelFavorite (bookId) {
        var _this = this
        this.$axios.post('/favorite/delete?bookId=' + bookId).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.favoriteBooks = _this.favoriteBooks.filter(book => book.id !== bookId)
            _this.$message.success('取消收藏成功')
          } else {
            _this.$message.error(resp.data.message)
          }
        })
      },
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage
      }
    }
  }
</script>

<style scoped>
  .nav-menu {
    box-shadow: 0 2px 4px 0 rgba(0,0,0,.05);
  }

  .favorites-container {
    max-width: 1300px;
    margin: 0 auto;
    padding: 20px;
  }

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

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }

  .login-tip, .empty-tip {
    text-align: center;
    padding: 50px;
    color: #666;
  }

  .login-tip p, .empty-tip p {
    margin-bottom: 20px;
    font-size: 16px;
  }
</style>
