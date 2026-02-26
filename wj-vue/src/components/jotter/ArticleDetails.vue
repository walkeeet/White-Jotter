<template>
  <div class="articles-area">
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <span v-if="article.author" style="margin-left: 20px;color: #909399;font-size: 14px">作者: {{article.author.username}}</span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
    </el-card>
    <div style="width: 990px;margin: 20px auto 0 auto">
      <jotter-comment :articleId="parseInt($route.query.id)"></jotter-comment>
    </div>
  </div>
</template>

<script>
  import JotterComment from './JotterComment.vue'
  export default {
    name: 'ArticleDetails',
    components: {
      JotterComment
    },
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
      }
    }
  }
</script>

<style scoped>
  @import "../../styles/markdown.css";
</style>
