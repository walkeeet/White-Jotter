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
              :type="isFavorite(item.id) ? 'danger' : 'default'"
              :icon="isFavorite(item.id) ? 'el-icon-star-on' : 'el-icon-star-off'"
              size="mini"
              circle
              @click.stop="toggleFavorite(item.id)">
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
        favoriteBookIds: []
      }
    },
    mounted: function () {
      this.loadBooks()
      this.loadFavorites()
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
      loadFavorites () {
        var _this = this
        this.$axios.get('/favorite/list').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.favoriteBookIds = resp.data.result.map(book => book.id)
          }
        })
      },
      isFavorite (bookId) {
        return this.favoriteBookIds.includes(bookId)
      },
      toggleFavorite (bookId) {
        var _this = this
        if (!this.$store.state.username) {
          this.$message.warning('请先登录后再收藏')
          this.$router.push('/login')
          return
        }
        if (this.isFavorite(bookId)) {
          this.$axios.post('/favorite/delete?bookId=' + bookId).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.favoriteBookIds = _this.favoriteBookIds.filter(id => id !== bookId)
              _this.$message.success('取消收藏成功')
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        } else {
          this.$axios.post('/favorite/add?bookId=' + bookId).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.favoriteBookIds.push(bookId)
              _this.$message.success('收藏成功')
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        }
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
