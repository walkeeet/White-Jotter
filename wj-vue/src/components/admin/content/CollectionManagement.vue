<template>
  <div>
    <el-card>
      <div slot="header">
        <span>我的收藏</span>
      </div>
      <div v-loading="loading">
        <el-row v-if="collections.length === 0" style="text-align: center; padding: 40px; color: #999;">
          暂无收藏的书籍
        </el-row>
        <el-row>
          <el-tooltip effect="dark" placement="right"
            v-for="item in collections.slice((currentPage-1)*pagesize,currentPage*pagesize)"
            :key="item.id">
            <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.book.title}}</p>
            <p slot="content" style="font-size: 13px;margin-bottom: 6px">
              <span>{{item.book.author}}</span> /
              <span>{{item.book.date}}</span> /
              <span>{{item.book.press}}</span>
            </p>
            <p slot="content" style="width: 300px" class="abstract">{{item.book.abs}}</p>
            <el-card style="width: 135px;margin-bottom: 20px;height: 270px;float: left;margin-right: 15px" class="book"
              bodyStyle="padding:10px" shadow="hover">
              <div class="cover">
                <img :src="item.book.cover" alt="封面">
              </div>
              <div class="info">
                <div class="title">
                  {{item.book.title}}
                </div>
              </div>
              <div class="author">{{item.book.author}}</div>
              <div class="collect-time">{{ formatDate(item.collectTime) }}</div>
              <div class="action">
                <el-button type="danger" size="mini" @click="removeCollection(item)">
                  取消收藏
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
            :total="collections.length">
          </el-pagination>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CollectionManagement',
  data () {
    return {
      collections: [],
      currentPage: 1,
      pagesize: 18,
      loading: false
    }
  },
  mounted: function () {
    this.loadCollections()
  },
  methods: {
    loadCollections () {
      var _this = this
      _this.loading = true
      this.$axios.get('/collection/list').then(resp => {
        _this.loading = false
        if (resp && resp.data.code === 200) {
          _this.collections = resp.data.result
        } else {
          _this.$message.error(resp.data.message)
        }
      })
    },
    removeCollection (item) {
      var _this = this
      this.$axios.post('/collection/remove?bid=' + item.bid).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$message.success('取消收藏成功')
          _this.collections = _this.collections.filter(c => c.id !== item.id)
        } else {
          _this.$message.error(resp.data.message)
        }
      })
    },
    handleCurrentChange (currentPage) {
      this.currentPage = currentPage
    },
    formatDate (dateStr) {
      if (!dateStr) return ''
      var date = new Date(dateStr)
      var year = date.getFullYear()
      var month = ('0' + (date.getMonth() + 1)).slice(-2)
      var day = ('0' + date.getDate()).slice(-2)
      return year + '-' + month + '-' + day
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
  }

  img {
    width: 115px;
    height: 172px;
  }

  .title {
    font-size: 14px;
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    text-align: left;
  }

  .collect-time {
    color: #999;
    font-size: 12px;
    text-align: left;
    margin-top: 3px;
  }

  .action {
    text-align: center;
    margin-top: 8px;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }
</style>
