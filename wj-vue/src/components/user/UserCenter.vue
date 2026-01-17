<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-card style="margin: 18px 2%;width: 95%">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="收藏书籍" name="collect">
          <el-row v-if="collectedBooks.length > 0">
            <el-tooltip effect="dark" placement="right"
                        v-for="item in collectedBooks"
                        :key="item.book.id">
              <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.book.title}}</p>
              <p slot="content" style="font-size: 13px;margin-bottom: 6px">
                <span>{{item.book.author}}</span> /
                <span>{{item.book.date}}</span> /
                <span>{{item.book.press}}</span>
              </p>
              <p slot="content" style="width: 300px" class="abstract">{{item.book.abs}}</p>
              <el-card style="width: 135px;margin-bottom: 20px;height: 233px;float: left;margin-right: 15px" class="book"
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
                <el-button
                  type="danger"
                  size="mini"
                  icon="el-icon-star-on"
                  @click="uncollectBook(item.book.id)"
                  class="collect-btn">
                  取消收藏
                </el-button>
              </el-card>
            </el-tooltip>
          </el-row>
          <el-empty v-else description="暂无收藏书籍"></el-empty>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserCenter',
  data () {
    return {
      activeTab: 'collect',
      collectedBooks: []
    }
  },
  mounted () {
    this.loadCollectedBooks()
  },
  methods: {
    loadCollectedBooks () {
      var _this = this
      this.$axios.get('/collect/list').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.collectedBooks = resp.data.result
        } else {
          _this.$message.error(resp.data.message)
        }
      })
    },
    uncollectBook (bookId) {
      var _this = this
      this.$axios.post('/uncollect?bookId=' + bookId).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$message.success(resp.data.result)
          _this.loadCollectedBooks()
        } else {
          _this.$message.error(resp.data.message)
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
    margin-bottom: 6px;
    text-align: left;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .collect-btn {
    width: 100%;
    margin-top: 5px;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
</style>
