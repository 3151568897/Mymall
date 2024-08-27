<template>
  <div>
    <el-upload
      action=""
      :http-request="upload"
      list-type="picture-card"
      :file-list="fileList"
      :on-remove="handleRemove"
      :on-success="handleUploadSuccess"
      :on-preview="handlePreview"
      :limit="maxCount"
      :on-exceed="handleExceed"
    >
      <i class="el-icon-plus"></i>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: 'multiUpload',
  props: {
    // 图片属性数组
    value: Array,
    // 最大上传图片数量
    maxCount: {
      type: Number,
      default: 30
    }
  },
  data () {
    return {
      dialogVisible: false,
      dialogImageUrl: null,
      url: null
    }
  },
  computed: {
    fileList () {
      let fileList = []
      for (let i = 0; i < this.value.length; i++) {
        if (this.value[i] !== undefined && this.value[i] != null && this.value[i] !== '') {
          fileList.push({ url: this.value[i] })
        }
      }

      return fileList
    }
  },
  mounted () {},
  methods: {
    upload (param) {
      const formData = new FormData()
      formData.append('file', param.file)
      this.$http({
        url: this.$http.adornUrl('/thirdParty/file/upload'),
        method: 'post',
        data: formData
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.url = data.data
          this.handleUploadSuccess(data, param.file)
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    emitInput (fileList) {
      let value = []
      for (let i = 0; i < fileList.length; i++) {
        value.push(fileList[i].url)
      }
      this.$emit('input', value)
    },
    handleRemove (file, fileList) {
      this.emitInput(fileList)
    },
    handlePreview (file) {
      this.dialogVisible = true
      this.dialogImageUrl = file.url
    },
    handleUploadSuccess (res, file) {
      console.log('上传成功...')
      this.fileList.push({
        name: file.name,
        // url: this.dataObj.host + "/" + this.dataObj.dir + "/" + file.name； 替换${filename}为真正的文件名
        // eslint-disable-next-line no-template-curly-in-string
        url: this.url
      })
      this.emitInput(this.fileList)
    },
    handleExceed (files, fileList) {
      this.$message({
        message: '最多只能上传' + this.maxCount + '张图片',
        type: 'warning',
        duration: 1000
      })
    }
  }
}
</script>
<style>
</style>


