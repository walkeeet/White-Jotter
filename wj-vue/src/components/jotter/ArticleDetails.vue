<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
          <div v-if="canDelete">
            <el-button type="danger" size="small" @click="deleteArticle">删除文章</el-button>
          </div>
        </div>
        <el-divider content-position="left">
          <span style="color: #909399;">
            {{article.articleDate}}
            <span v-if="article.author" style="margin-left: 10px;">作者: {{article.author.username}}</span>
            <span style="margin-left: 10px;">评论: {{article.commentCount || 0}}</span>
          </span>
        </el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
    </el-card>
    <!-- 评论组件 -->
    <article-comments :article-id="parseInt($route.query.id)"></article-comments>
  </div>
</template>

<script>
import ArticleComments from './ArticleComments'

export default {
  name: 'ArticleDetails',
  components: {
    ArticleComments
  },
  data () {
    return {
      article: {},
      currentUser: null
    }
  },
  computed: {
    canDelete () {
      return this.currentUser && this.article.author &&
             this.currentUser.id === this.article.author.id
    }
  },
  mounted () {
    this.loadArticle()
    this.loadCurrentUser()
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
    loadCurrentUser () {
      if (this.$store.state.username && this.$store.state.username !== '') {
        var _this = this
        this.$axios.get('/user/' + this.$store.state.username).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.currentUser = resp.data.result
          }
        })
      }
    },
    deleteArticle () {
      this.$confirm('确定删除这篇文章吗？删除后不可恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        var _this = this
        this.$axios.delete('/articles/' + this.article.id).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.$message.success('删除成功')
            _this.$router.push('/jotter')
          } else {
            _this.$message.error(resp.data.message || '删除失败')
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
@import "../../styles/markdown.css";
</style>
