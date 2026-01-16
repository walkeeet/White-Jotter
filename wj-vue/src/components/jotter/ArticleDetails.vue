<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
        <div class="like-section">
          <el-button
            :type="hasLiked ? 'danger' : 'default'"
            icon="el-icon-thumb"
            @click="handleLike"
          >
            {{ hasLiked ? '取消点赞' : '点赞' }}
            <span style="margin-left: 5px">{{ likeCount }}</span>
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: [],
        likeCount: 0,
        hasLiked: false
      }
    },
    mounted () {
      this.loadArticle()
      this.loadLikeInfo()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
          }
        })
      },
      loadLikeInfo () {
        var _this = this
        this.$axios.get('/article/like/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.likeCount = resp.data.result.likeCount
            _this.hasLiked = resp.data.result.hasLiked || false
          }
        })
      },
      handleLike () {
        var _this = this
        this.$axios.post('/article/like/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.likeCount = resp.data.result.likeCount
            _this.hasLiked = resp.data.result.hasLiked
          } else if (resp && resp.data.code === 400) {
            // 未登录提示
            _this.$alert('请先登录', '提示', {
              confirmButtonText: '确定',
              callback: action => {
                _this.$router.push('/login')
              }
            })
          }
        }).catch(err => {
          if (err.response && err.response.data && err.response.data.code === 400) {
            _this.$alert('请先登录', '提示', {
              confirmButtonText: '确定',
              callback: action => {
                _this.$router.push('/login')
              }
            })
          }
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";

  .like-section {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
  }
</style>
