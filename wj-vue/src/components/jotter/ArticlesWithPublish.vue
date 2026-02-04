<template>
  <div style="margin-top: 40px">
    <!-- 发布按钮 -->
    <div style="width: 990px; margin: 0 auto 20px auto; text-align: right;">
      <el-button type="primary" icon="el-icon-edit" @click="goToPublish" v-if="isLoggedIn">
        发布笔记
      </el-button>
      <el-button type="primary" icon="el-icon-edit" @click="goToLogin" v-else>
        登录后发布笔记
      </el-button>
    </div>

    <div class="articles-area">
      <el-card style="text-align: left">
        <div v-for="article in articles" :key="article.id">
          <div style="float:left;width:85%;height: 150px;">
            <router-link class="article-link" :to="{path:'jotter/article',query:{id: article.id}}"><span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span></router-link>
            <el-divider content-position="left">{{article.articleDate}}</el-divider>
            <router-link class="article-link" :to="{path:'jotter/article',query:{id: article.id}}"><p>{{article.articleAbstract}}</p></router-link>
            <div class="article-meta">
              <span class="comment-count">
                <i class="el-icon-chat-dot-round"></i> {{article.commentCount || 0}} 评论
              </span>
            </div>
          </div>
          <el-image
            style="margin:18px 0 0 30px;width:100px;height: 100px"
            :src="article.articleCover"
            fit="cover"></el-image>
          <el-divider></el-divider>
        </div>
      </el-card>
    </div>
    <el-pagination
      background
      layout="total, prev, pager, next, jumper"
      @current-change="handleCurrentChange"
      :page-size="pageSize"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>

  export default {
    name: 'ArticlesWithPublish',
    data () {
      return {
        articles: [],
        pageSize: 4,
        total: 0,
        isLoggedIn: false
      }
    },
    mounted () {
      this.checkLoginStatus()
      this.loadArticles()
    },
    methods: {
      checkLoginStatus() {
        const username = localStorage.getItem('username')
        this.isLoggedIn = !!username
      },
      loadArticles () {
        var _this = this
        this.$axios.get('/article/' + this.pageSize + '/1').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.articles = resp.data.result.content
            _this.total = resp.data.result.totalElements
            // Load comment counts for each article
            _this.loadCommentCounts()
          }
        })
      },
      loadCommentCounts() {
        var _this = this
        this.articles.forEach(article => {
          this.$axios.get('/comment/count/' + article.id).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$set(article, 'commentCount', resp.data.result)
            }
          })
        })
      },
      handleCurrentChange (page) {
        var _this = this
        this.$axios.get('/article/' + this.pageSize + '/' + page).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.articles = resp.data.result.content
            _this.total = resp.data.result.totalElements
            // Load comment counts for each article
            _this.loadCommentCounts()
          }
        })
      },
      goToPublish() {
        this.$router.push('/jotter/publish')
      },
      goToLogin() {
        this.$router.push('/login')
      }
    }
  }
</script>

<style scoped>
  .articles-area {
    width: 990px;
    height: 750px;
    margin-left: auto;
    margin-right: auto;
  }

  .article-link {
    text-decoration: none;
    color: #606266;
  }

  .article-link:hover {
    color: #409EFF;
  }

  .article-meta {
    margin-top: 10px;
  }

  .comment-count {
    color: #909399;
    font-size: 13px;
  }

  .comment-count i {
    margin-right: 3px;
  }
</style>
