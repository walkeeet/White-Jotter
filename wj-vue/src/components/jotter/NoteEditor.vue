<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home'}">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/notes'}">我的笔记</el-breadcrumb-item>
        <el-breadcrumb-item>编辑器</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row>
      <el-input
        v-model="note.title"
        style="margin: 10px 0px;font-size: 18px;"
        placeholder="请输入标题"></el-input>
    </el-row>
    <el-row style="height: calc(100vh - 140px);">
      <mavon-editor
        v-model="note.contentMd"
        style="height: 100%;"
        ref=md
        @save="saveNote"
        fontSize="16px">
      </mavon-editor>
    </el-row>
  </div>
</template>

<script>
  export default {
    name: 'NoteEditor',
    data () {
      return {
        note: {
          id: null,
          title: '',
          contentMd: '',
          contentHtml: ''
        }
      }
    },
    mounted () {
      if (this.$route.params.note) {
        this.note = this.$route.params.note
      }
    },
    methods: {
      saveNote (value, render) {
        this.$confirm('是否保存并发布笔记?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            this.$axios
              .post('/note', {
                id: this.note.id,
                title: this.note.title,
                contentMd: value,
                contentHtml: render
              }).then(resp => {
              if (resp && resp.data.code === 200) {
                this.$message({
                  type: 'success',
                  message: '保存成功'
                })
                this.$router.push('/notes')
              } else {
                this.$message({
                  type: 'error',
                  message: resp.data.message || '保存失败'
                })
              }
            })
          }
        ).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消发布'
          })
        })
      }
    }
  }
</script>
