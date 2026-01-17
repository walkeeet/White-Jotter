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
          <div class="collection-btn">
            <el-button
              :type="isCollected(item.id) ? 'warning' : 'primary'"
              size="mini"
              @click="toggleCollection(item.id)"
              :loading="collectionLoading[item.id]">
              {{ isCollected(item.id) ? '已收藏' : '收藏' }}
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
        collectedBooks: new Set(),
        collectionLoading: {}
      }
    },
    mounted: function () {
      this.loadBooks()
      this.loadCollectedBooks()
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
      loadCollectedBooks () {
        if (!this.$store.state.username) {
          return
        }
        var _this = this
        this.$axios.get('/api/collection/my').then(resp => {
          if (resp && resp.data.code === 200) {
            resp.data.result.forEach(item => {
              _this.collectedBooks.add(item.book.id)
            })
          }
        }).catch(() => {
          // 用户未登录
        })
      },
      isCollected (bookId) {
        return this.collectedBooks.has(bookId)
      },
      toggleCollection (bookId) {
        if (!this.$store.state.username) {
          this.$message.warning('请先登录')
          this.$router.push('/login')
          return
        }

        this.collectionLoading[bookId] = true
        const isCollected = this.isCollected(bookId)
        const url = isCollected ? '/api/collection/remove' : '/api/collection/add'

        this.$axios.post(url, { bookId: bookId }).then(resp => {
          if (resp && resp.data.code === 200) {
            if (isCollected) {
              this.collectedBooks.delete(bookId)
              this.$message.success('已取消收藏')
            } else {
              this.collectedBooks.add(bookId)
              this.$message.success('收藏成功')
            }
          } else {
            this.$message.error(resp.data.message)
          }
        }).catch(() => {
          this.$message.error('操作失败')
        }).finally(() => {
          this.collectionLoading[bookId] = false
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

  .collection-btn {
    margin-top: 5px;
    text-align: center;
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

</style>
