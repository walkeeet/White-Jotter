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
        <el-card style="width: 135px;margin-bottom: 20px;height: 260px;float: left;margin-right: 15px" class="book"
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
          <div class="collection-btn">
            <el-button type="text" @click.stop="handleCollection(item)"
              :class="item.isCollected ? 'collected' : ''"
              :icon="item.isCollected ? 'el-icon-star-on' : 'el-icon-star-off'">
              {{ item.isCollected ? '已收藏' : '收藏' }}
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
        pagesize: 18
      }
    },
    watch: {
      books: function (newBooks, oldBooks) {
        if (newBooks && newBooks.length > 0) {
          this.loadCollectionStatus()
        }
      }
    },
    mounted: function () {
      this.loadBooks()
    },
    methods: {
      loadBooks () {
        var _this = this
        this.$axios.get('/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
            _this.loadCollectionStatus()
          }
        })
      },
      loadCollectionStatus () {
        var _this = this
        if (!_this.$store.state.username) {
          return
        }
        _this.books.forEach(book => {
          _this.$axios.get('/collection/status?bid=' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              book.isCollected = resp.data.result
            }
          })
        })
      },
      handleCollection (book) {
      var _this = this
      if (!_this.$store.state.username) {
        _this.$router.push({path: '/login', query: {redirect: '/library'}})
        return
      }
        if (book.isCollected) {
          _this.$axios.post('/collection/remove?bid=' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              book.isCollected = false
              _this.$message('取消收藏成功')
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        } else {
          _this.$axios.post('/collection/add?bid=' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              book.isCollected = true
              _this.$message('收藏成功')
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
            _this.loadCollectionStatus()
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

  .collection-btn {
    text-align: center;
    margin-top: 5px;
  }

  .collection-btn .el-button {
    font-size: 12px;
    color: #999;
  }

  .collection-btn .el-button.collected {
    color: #409eff;
  }

  .collection-btn .el-button:hover {
    color: #409eff;
  }

</style>
