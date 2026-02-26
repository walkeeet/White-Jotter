<template>
  <div class="article-publish">
    <el-card class="publish-card">
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;">发布笔记</span>
      </div>
      <el-form :model="articleForm" :rules="rules" ref="articleForm" label-width="80px">
        <el-form-item label="标题" prop="articleTitle">
          <el-input v-model="articleForm.articleTitle" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        <el-form-item label="摘要" prop="articleAbstract">
          <el-input
            type="textarea"
            :rows="2"
            v-model="articleForm.articleAbstract"
            placeholder="请输入文章摘要">
          </el-input>
        </el-form-item>
        <el-form-item label="封面" prop="articleCover">
          <el-input v-model="articleForm.articleCover" placeholder="请输入封面图片URL"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="articleContentMd">
          <mavon-editor
            v-model="articleForm.articleContentMd"
            ref="md"
            @change="handleEditorChange"
            @imgAdd="handleImgAdd"
            style="min-height: 400px">
          </mavon-editor>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">发布</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ArticlePublish',
  data () {
    return {
      articleForm: {
        articleTitle: '',
        articleAbstract: '',
        articleCover: '',
        articleContentMd: '',
        articleContentHtml: ''
      },
      rules: {
        articleTitle: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        articleAbstract: [
          { required: true, message: '请输入文章摘要', trigger: 'blur' },
          { max: 500, message: '摘要不能超过500个字符', trigger: 'blur' }
        ],
        articleContentMd: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleEditorChange (value, render) {
      this.articleForm.articleContentHtml = render
    },
    handleImgAdd (pos, $file) {
      var _this = this
      var formdata = new FormData()
      formdata.append('image', $file)
      this.$axios.post('/admin/content/books/covers', formdata, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$refs.md.$img2Url(pos, resp.data.result)
        }
      })
    },
    submitForm () {
      this.$refs.articleForm.validate((valid) => {
        if (valid) {
          var _this = this
          this.$axios.post('/articles/publish', this.articleForm).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success('发布成功')
              _this.resetForm()
              _this.$router.push('/jotter')
            } else {
              _this.$message.error(resp.data.message || '发布失败')
            }
          }).catch(() => {
            _this.$message.error('发布失败，请检查是否已登录')
          })
        } else {
          return false
        }
      })
    },
    resetForm () {
      this.$refs.articleForm.resetFields()
      this.articleForm.articleContentHtml = ''
    }
  }
}
</script>

<style scoped>
.article-publish {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.publish-card {
  text-align: left;
}
</style>
