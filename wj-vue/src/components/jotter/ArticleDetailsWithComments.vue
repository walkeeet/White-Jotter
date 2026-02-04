<template>
  <div>
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
    </div>
    <!-- 评论区域 -->
    <comment-section :article-id="articleId"></comment-section>
  </div>
</template>

<script>
  import CommentSection from './CommentSection.vue'

  export default {
    name: 'ArticleDetailsWithComments',
    components: {
      CommentSection
    },
    data () {
      return {
        article: [],
        articleId: null
      }
    },
    mounted () {
      this.articleId = parseInt(this.$route.query.id)
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
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";

  .articles-area {
    margin-bottom: 20px;
  }
</style>
