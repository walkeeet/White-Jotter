<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
        <div style="margin-top: 20px;">
          <el-button
            :type="liked ? 'danger' : 'default'"
            :icon="'el-icon-thumb'"
            circle
            @click="handleLike"
          >
          </el-button>
          <span style="margin-left: 10px;">{{likeCount}} 赞</span>
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
        liked: false
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
            _this.likeCount = resp.data.result.count
            _this.liked = resp.data.result.userStatus === 1
          }
        })
      },
      handleLike () {
        var _this = this
        this.$axios.post('/article/like/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.likeCount = resp.data.result.count
            _this.liked = resp.data.result.status === 1
          } else {
            _this.$message.warning(resp.data.message || '操作失败')
            if (resp.data.message === '请先登录') {
              _this.$router.push('/login')
            }
          }
        }).catch(err => {
          console.log(err)
          this.$message.error('网络错误')
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";
</style>
