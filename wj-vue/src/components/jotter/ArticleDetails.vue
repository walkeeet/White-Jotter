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
            type="primary" 
            :icon="isLiked ? 'el-icon-star-on' : 'el-icon-star-off'" 
            :class="{'liked': isLiked}"
            @click="handleLike"
            circle
            size="medium">
          </el-button>
          <span class="like-count">{{ likeCount }}</span>
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
        isLiked: false,
        likeCount: 0
      }
    },
    mounted () {
      this.loadArticle()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.article = resp.data.result
            _this.loadLikeStatus()
          }
        })
      },
      loadLikeStatus () {
        var _this = this
        this.$axios.get('/like/status/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.isLiked = resp.data.result.isLiked
            _this.likeCount = resp.data.result.likeCount
          }
        })
      },
      handleLike () {
        var _this = this
        this.$axios.post('/like/' + this.$route.query.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.isLiked = resp.data.result.isLiked
            _this.likeCount = resp.data.result.likeCount
          } else {
            _this.$message.error(resp.data.message || '请先登录')
          }
        }).catch(error => {
          if (error.response && error.response.status === 401) {
            _this.$message.error('请先登录')
          } else {
            _this.$message.error('操作失败，请稍后重试')
          }
        })
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";
  
  .like-section {
    margin-top: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .like-count {
    margin-left: 10px;
    font-size: 16px;
    color: #606266;
  }
  
  .liked {
    background-color: #f56c6c !important;
    border-color: #f56c6c !important;
  }
</style>
