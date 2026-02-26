<template>
  <div style="margin-top: 40px">
    <div class="articles-header">
      <el-button v-if="isLoggedIn" type="primary" @click="publishArticle">发布笔记</el-button>
    </div>
    <div class="articles-area">
      <el-card style="text-align: left">
        <div v-for="article in articles" :key="article.id">
          <div style="float:left;width:85%;height: 150px;">
            <router-link class="article-link" :to="{path:'jotter/article',query:{id: article.id}}">
              <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
            </router-link>
            <el-divider content-position="left">
              <span style="color: #909399; font-size: 12px;">
                {{article.articleDate}}
                <span v-if="article.author" style="margin-left: 10px;">作者: {{article.author.username}}</span>
                <span style="margin-left: 10px;">评论: {{article.commentCount || 0}}</span>
              </span>
            </el-divider>
            <router-link class="article-link" :to="{path:'jotter/article',query:{id: article.id}}">
              <p>{{article.articleAbstract}}</p>
            </router-link>
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
  name: 'Articles',
  data () {
    return {
      articles: [],
      pageSize: 4,
      total: 0
    }
  },
  computed: {
    isLoggedIn () {
      return this.$store.state.username && this.$store.state.username !== ''
    }
  },
  mounted () {
    this.loadArticles()
  },
  methods: {
    loadArticles () {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/1').then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.result.content
          _this.total = resp.data.result.totalElements
        }
      })
    },
    handleCurrentChange (page) {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.articles = resp.data.result.content
          _this.total = resp.data.result.totalElements
        }
      })
    },
    publishArticle () {
      this.$router.push('/jotter/publish')
    }
  }
}
</script>

<style scoped>
.articles-header {
  width: 990px;
  margin: 0 auto 20px;
  text-align: right;
}

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
</style>
