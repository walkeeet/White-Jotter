<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
    </el-card>
    <el-card style="text-align: left;width: 990px;margin: 20px auto 0 auto">
      <comment-section :article-id="article.id" @comment-added="loadCommentCount"></comment-section>
    </el-card>
  </div>
</template>

<script>
import CommentSection from './CommentSection'

export default {
  name: 'ArticleDetails',
  components: { CommentSection },
  data () {
    return {
      article: []
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
        }
      })
    },
    loadCommentCount () {
    }
  }
}
</script>

<style scoped>
  @import "../../styles/markdown.css";
</style>
