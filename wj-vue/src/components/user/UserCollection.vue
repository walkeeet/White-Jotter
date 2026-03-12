<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>个人中心</el-breadcrumb-item>
        <el-breadcrumb-item>我的收藏</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-card style="margin: 18px 2%;width: 95%">
      <div slot="header" class="clearfix">
        <span>我的收藏书籍</span>
        <el-tag type="info" style="margin-left: 10px">共 {{ collectedBooks.length }} 本</el-tag>
      </div>
      <div v-if="collectedBooks.length === 0" style="text-align: center; padding: 50px;">
        <el-empty description="您还没有收藏任何书籍"></el-empty>
        <el-button type="primary" @click="$router.push('/library')">去图书馆看看</el-button>
      </div>
      <el-row v-else>
        <el-tooltip effect="dark" placement="right"
                    v-for="item in collectedBooks.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                    :key="item.id">
          <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
          <p slot="content" style="font-size: 13px;margin-bottom: 6px">
            <span>{{item.author}}</span> /
            <span>{{item.date}}</span> /
            <span>{{item.press}}</span>
          </p>
          <p slot="content" style="width: 300px" class="abstract">{{item.abs}}</p>
          <el-card style="width: 135px;margin-bottom: 20px;height: 260px;float: left;margin-right: 15px" class="book"
                   bodyStyle="padding:10px" shadow="hover">
            <div class="cover">
              <img :src="item.cover" alt="封面">
            </div>
            <div class="info">
              <div class="title">
                <a href="">{{item.title}}</a>
              </div>
            </div>
            <div class="author">{{item.author}}</div>
            <el-button 
              type="danger" 
              size="mini" 
              class="collect-btn"
              @click.stop="removeCollection(item)">
              <i class="el-icon-star-off"></i>
              取消收藏
            </el-button>
          </el-card>
        </el-tooltip>
      </el-row>
      <el-row v-if="collectedBooks.length > 0">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          :total="collectedBooks.length"
          layout="total, prev, pager, next, jumper">
        </el-pagination>
      </el-row>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'UserCollection',
    data () {
      return {
        collectedBooks: [],
        currentPage: 1,
        pagesize: 18
      }
    },
    mounted () {
      this.loadCollectedBooks()
    },
    methods: {
      loadCollectedBooks () {
        var _this = this
        this.$axios.get('/collection/list').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.collectedBooks = resp.data.result
          } else if (resp.data.code === 401) {
            _this.$message.error('请先登录')
            _this.$router.push({ path: '/login', query: { redirect: '/user/collection' } })
          }
        })
      },
      removeCollection (book) {
        var _this = this
        this.$confirm('确定要取消收藏这本书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.$axios.post('/collection/remove/' + book.id).then(resp => {
            if (resp && resp.data.code === 200) {
              _this.$message.success(resp.data.message)
              _this.loadCollectedBooks()
            } else {
              _this.$message.error(resp.data.message)
            }
          })
        })
      },
      handleCurrentChange (currentPage) {
        this.currentPage = currentPage
      }
    }
  }
</script>

<style scoped>
  .cover {
    width: 115px;
    height: 172px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
  }

  img {
    width: 115px;
    height: 172px;
  }

  .title {
    font-size: 14px;
    text-align: left;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    margin-bottom: 8px;
    text-align: left;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .collect-btn {
    width: 100%;
    margin-top: 5px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }
</style>
