<template>
  <div>
    <el-row style="height: 840px;">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
      <view-switch class="switch"></view-switch>
      <el-tooltip effect="dark" placement="right"
                  v-for="item in books.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                  :key="item.id">
        <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
        <p slot="content" style="font-size: 13px;margin-bottom: 6px">
          <span>{{item.author}}</span> /
          <span>{{item.date}}</span> /
          <span>{{item.press}}</span>
        </p>
        <p slot="content" style="width: 300px" class="abstract">{{item.abs}}</p>
        <el-card style="width: 135px;margin-bottom: 20px;height: 233px;float: left;margin-right: 15px" class="book"
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
              :type="favoriteIds.includes(item.id) ? 'danger' : 'primary'"
              size="mini"
              :icon="favoriteIds.includes(item.id) ? 'el-icon-star-on' : 'el-icon-star-off'"
              @click.stop="toggleFavorite(item.id)">
              {{ favoriteIds.includes(item.id) ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </el-card>
      </el-tooltip>
    </el-row>
    <el-row>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="books.length">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
  import SearchBar from './SearchBar'
  import ViewSwitch from './ViewSwitch'

  export default {
    name: 'Books',
    components: {SearchBar, ViewSwitch},
    data () {
      return {
        books: [],
        currentPage: 1,
        pagesize: 18,
        favoriteIds: []
      }
    },
    mounted: function () {
      this.loadBooks()
      this.loadFavoriteIds()
    },
    computed: {
      username () {
        return this.$store.state.username
      }
    },
    methods: {
      loadBooks () {
        var _this = this
        this.$axios.get('/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
          }
        })
      },
      loadFavoriteIds () {
        var _this = this
        if (!this.username) {
          _this.favoriteIds = []
          return
        }
        this.$axios.get('/book/favorite/ids').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.favoriteIds = resp.data.result
          }
        }).catch(() => {
          _this.favoriteIds = []
        })
      },
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage
      },
      searchResult () {
        var _this = this
        this.$axios
          .get('/search?keywords=' + this.$refs.searchBar.keywords, {
          }).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
          }
        })
      },
      toggleFavorite (bid) {
        var _this = this
        if (!this.username) {
          this.$alert('请先登录后再收藏书籍', '提示', {
            confirmButtonText: '确定',
            callback: action => {
              _this.$router.push({ path: '/login', query: { redirect: _this.$route.fullPath } })
            }
          })
          return
        }
        if (this.favoriteIds.includes(bid)) {
          this.$axios.post('/book/unfavorite', { bid: bid }).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('取消收藏成功')
              var index = _this.favoriteIds.indexOf(bid)
              if (index > -1) {
                _this.favoriteIds.splice(index, 1)
              }
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        } else {
          this.$axios.post('/book/favorite', { bid: bid }).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('收藏成功')
              _this.favoriteIds.push(bid)
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        }
      }
    },
    watch: {
      username (newVal) {
        if (newVal) {
          this.loadFavoriteIds()
        } else {
          this.favoriteIds = []
        }
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
    /*margin: 0 auto;*/
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

  .el-icon-delete {
    cursor: pointer;
    float: right;
  }

  .switch {
    display: flex;
    position: absolute;
    left: 780px;
    top: 25px;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }

  .favorite-btn {
    text-align: center;
    margin-top: 5px;
  }

</style>
