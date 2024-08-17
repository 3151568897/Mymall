<template>
  <div>
    <el-switch
      v-model="draggable"
      active-text="开启拖拽"
      inactive-text="关闭拖拽">
    </el-switch>
    <el-button v-if="draggable" type="primary" @click="batchSave">
      批量保存
    </el-button>
    <el-button type="danger" plain @click="batchDelete">批量删除</el-button>
    <el-tree :data="menus" :props="defaultProps" show-checkbox
             :expand-on-click-node="false" node-key="catId"
             :default-expanded-keys=expandedKeys
             :draggable="draggable"
             :allow-drop="allowDrop"
             @node-drop="handleDrop"
             ref="menuTree">
    <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="text"
            size="mini"
            v-if="node.level !== 3"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="() => edit(data)">
            edit
          </el-button>
          <el-button
            type="text"
            size="mini"
            v-if="node.childNodes.length === 0"
            @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="30%">
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitData">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      maxLevel: 0,
      updateNodes: [],
      draggable: false,
      menus: [],
      pCid: [],
      expandedKeys: [],
      category: {
        catId: null,
        name: '',
        parentCid: '',
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        productUnit: '',
        icon: ''
      },
      dialogVisible: false,
      dialogType: '',
      dialogTitle: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  methods: {
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.menus = data.data
        } else {
          this.menus = []
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    batchDelete () {
      let checkedNodes = this.$refs.menuTree.getCheckedNodes()
      let catIds = []
      for (let i = 0; i < checkedNodes.length; i++) {
        catIds.push(checkedNodes[i].catId)
      }
      this.$confirm(`是否批量删除【${catIds}】级分类?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(catIds, false)
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500
            })
            this.getMenus()
          } else {
            this.$message.error(data.msg)
          }
        }).catch((error) => {
          console.log(error)
        })
      })
    },
    handleDrop (draggingNode, dropNode, dropType, ev) {
      let siblings = null
      let pCid = 0

      if (dropType === 'inner') {
        pCid = dropNode.data.catId
        siblings = dropNode.childNodes
      } else {
        pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
        siblings = dropNode.parent.childNodes
      }
      this.pCid.push(pCid)
      // 给兄弟节点排序
      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId === draggingNode.data.catId) {
          // 如果遍历的是当前正在拖拽的节点
          let catLevel = draggingNode.level
          if (siblings[i].level !== draggingNode.level) {
            // 当前节点的层级发送变化
            catLevel = siblings[i].level
            // 修改子节点的层级
            this.updateChildNodeLevel(siblings[i])
          }
          this.updateNodes.push({catId: siblings[i].data.catId, sort: i, parentCid: pCid, catLevel: catLevel})
        } else {
          this.updateNodes.push({catId: siblings[i].data.catId, sort: i})
        }
      }
    },
    batchSave () {
      this.$http({
        url: this.$http.adornUrl('/product/category/update/sort'),
        method: 'post',
        data: this.$http.adornData(this.updateNodes, false)
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.$message({
            message: '菜单顺序修改成功',
            type: 'success'
          })
          this.getMenus()
          // 刷新数据,展开影响菜单
          this.expandedKeys = this.pCid
          this.updateNodes = []
          this.maxLevel = 0
          this.pCid = []
        } else {
          this.$message.error(data.msg)
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    updateChildNodeLevel (node) {
      if (node.childNodes && node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          let cNode = node.childNodes[i].data
          this.updateNodes.push({catId: cNode.catId, catLevel: node.childNodes[i].level})
          this.updateChildNodeLevel(node.childNodes[i])
        }
      }
    },
    allowDrop (draggingNode, dropNode, type) {
      // 被拖动的节点以及所在父节点总层数不能大于3点
      this.countNodeLevel(draggingNode)
      // 当前拖动的节点+所在父节点最大层级小于4
      let deep = Math.abs(this.maxLevel - draggingNode.level) + 1
      if (type === 'inner') {
        return (deep + dropNode.level) <= 3
      } else {
        return (deep + dropNode.parent.level) <= 3
      }
    },
    countNodeLevel (node) {
      // 总层数
      if (node.childNodes && node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          if (node.childNodes[i].level > this.maxLevel) {
            this.maxLevel = node.childNodes[i].level
          }
          this.countNodeLevel(node.childNodes[i])
        }
      } else {
        return 0
      }
    },
    append (data) {
      this.category.catId = null
      this.category.name = null
      this.category.icon = null
      this.category.productUnit = null
      this.dialogTitle = '添加分类'
      this.dialogType = 'add'
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
      this.dialogVisible = true
    },
    edit (data) {
      this.dialogTitle = '修改分类'
      this.dialogType = 'edit'
      this.dialogVisible = true

      // 发送请求 获取最新的数据
      this.$http({
        url: this.$http.adornUrl('/product/category/info/' + data.catId),
        method: 'get'
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.category.catId = data.data.catId
          this.category.name = data.data.name
          this.category.icon = data.data.icon
          this.category.productUnit = data.data.productUnit
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    submitData () {
      if (this.dialogType === 'add') {
        this.addCategory()
      } else if (this.dialogType === 'edit') {
        this.editCategory()
      }
    },
    remove (node, data) {
      this.$confirm(`是否删除[${data.name}]菜单?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        var ids = [data.catId]
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500
            })
            // 刷新菜单
            this.getMenus()
            // 展开默认展开的菜单
            this.expandedKeys = [node.parent.data.catId !== undefined ? node.parent.data.catId : 0]
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    addCategory () {
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.$http.adornData(this.category, false)
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500
          })
          this.getMenus()
          this.expandedKeys = [this.category.parentCid]
          this.dialogVisible = false
        } else {
          this.$message.error(data.msg)
        }
      }).catch((error) => {
        console.log(error)
      })
    },
    editCategory () {
      var {catId, name, icon, productUnit} = this.category
      var data = {catId, name, icon, productUnit}
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'post',
        data: this.$http.adornData(data, false)
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.$message({
            message: '菜单修改成功',
            type: 'success',
            duration: 1500
          })
          this.getMenus()
          this.expandedKeys = [this.category.parentCid]
          this.dialogVisible = false
        } else {
          this.$message.error(data.msg)
        }
      }).catch((error) => {
        console.log(error)
      })
    }
  },
  created () {
    this.getMenus()
    console.log(this.menus)
  }
}
</script>

<style scoped lang="scss">

</style>
