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
          <el-button 
            :type="collectionStatus[item.id] ? 'danger' : 'primary'" 
            size="mini" 
            class="collect-btn"
            @click.stop="toggleCollection(item)">
            <i :class="collectionStatus[item.id] ? 'el-icon-star-off' : 'el-icon-star-on'"></i>
            {{ collectionStatus[item.id] ? '已收藏' : '收藏' }}
          </el-button>
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
        collectionStatus: {}
      }
    },
    mounted: function () {
      this.loadBooks()
    },
    computed: {
      isLoggedIn () {
        return this.$store.state.username !== ''
      }
    },
    methods: {
      loadBooks () {
        var _this = this
        this.$axios.get('/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
            _this.checkCollectionStatus()
          }
        })
      },
      checkCollectionStatus () {
        if (!this.isLoggedIn) {
          return
        }
        var _this = this
        this.books.forEach(book => {
          _this.$axios.get('/collection/check/' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$set(_this.collectionStatus, book.id, resp.data.result)
            }
          })
        })
      },
      toggleCollection (book) {
        if (!this.isLoggedIn) {
          this.$confirm('请先登录后再收藏书籍', '提示', {
            confirmButtonText: '去登录',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.$router.push({ path: '/login', query: { redirect: '/library' } })
          })
          return
        }
        
        var _this = this
        if (this.collectionStatus[book.id]) {
          this.$axios.post('/collection/remove/' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$set(_this.collectionStatus, book.id, false)
              _this.$message.success(resp.data.message)
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        } else {
          this.$axios.post('/collection/add/' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$set(_this.collectionStatus, book.id, true)
              _this.$message.success(resp.data.message)
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
            _this.checkCollectionStatus()
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
  }

  .title {
    font-size: 14px;
    text-align: left;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    margin-bottom: 8px;
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

  .collect-btn {
    width: 100%;
    margin-top: 5px;
  }

</style>
