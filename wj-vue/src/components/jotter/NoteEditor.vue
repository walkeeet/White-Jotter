<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home'}">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/mynotes'}">我的笔记</el-breadcrumb-item>
        <el-breadcrumb-item>编辑笔记</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row>
      <el-input
        v-model="note.title"
        style="margin: 10px 0px;font-size: 18px;"
        placeholder="请输入笔记标题"></el-input>
    </el-row>
    <el-row style="height: calc(100vh - 140px);">
      <mavon-editor
        v-model="note.contentMd"
        style="height: 100%;"
        ref=md
        @save="saveNote"
        fontSize="16px">
        <button type="button" class="op-icon el-icon-document" :title="'摘要/封面'" slot="left-toolbar-after"
                @click="dialogVisible = true"></button>
      </mavon-editor>
      <el-dialog
        :visible.sync="dialogVisible"
        width="30%">
        <el-divider content-position="left">摘要</el-divider>
        <el-input
          type="textarea"
          v-model="note.noteAbstract"
          rows="6"
          maxlength="255"
          show-word-limit></el-input>
        <el-divider content-position="left">封面</el-divider>
        <div style="margin-top: 20px">
          <el-input v-model="note.noteCover" autocomplete="off" placeholder="图片 URL"></el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>
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
        contentHtml: '',
        noteAbstract: '',
        noteCover: ''
      },
      dialogVisible: false
    }
  },
  mounted () {
    const noteId = this.$route.query.id
    if (noteId) {
      this.loadNote(noteId)
    }
  },
  methods: {
    loadNote (id) {
      this.$axios.get(`/user/note/${id}`).then(resp => {
        if (resp && resp.data.code === 200) {
          this.note = resp.data.result
        } else {
          this.$message.error(resp.data.message || '加载笔记失败')
        }
      })
    },
    saveNote (value, render) {
      if (!this.note.title) {
        this.$message.warning('请输入笔记标题')
        return
      }
      this.$confirm('是否保存笔记?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
          .post('/user/note', {
            id: this.note.id,
            title: this.note.title,
            contentMd: value,
            contentHtml: render,
            noteAbstract: this.note.noteAbstract,
            noteCover: this.note.noteCover
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message({
                type: 'success',
                message: '保存成功'
              })
              this.$router.push('/mynotes')
            } else {
              this.$message.error(resp.data.message || '保存失败')
            }
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消保存'
        })
      })
    }
  }
}
</script>
