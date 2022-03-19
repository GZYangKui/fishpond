<template>
  <el-row justify="center" align="middle" style="height: 100%">
    <el-form label-width="100px">
      <el-form-item label="邮箱地址:">
        <el-input type="email" v-model="registerInfo.email"/>
      </el-form-item>
      <el-form-item label="验证码:">
        <el-input v-model="registerInfo.code">
          <template #append>
            <el-button @click="handleGetCode">获取</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-row justify="end" style="width: 100%">
          <el-button>确定注册</el-button>
        </el-row>
      </el-form-item>
    </el-form>
    <el-dialog v-model="dialogVisible" title="发送验证码" width="30%">
      <el-form label-width="100px">
        <el-form-item>
          <div style="width: 200px;height: 50px">
            <el-image :src="base64Str" @click="handleGetCode"/>
          </div>
        </el-form-item>
        <el-form-item>
          <el-input/>
        </el-form-item>
        <el-form-item>
          <el-button>发送</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-row>
</template>

<script>
import {code} from "../../api/Kapt";

const defaultRegisterInfo = {
  email: null,
  code: null
};
export default {
  name: "Index",
  data() {
    return {
      base64Str: null,
      dialogVisible: false,
      registerInfo: Object.assign({}, defaultRegisterInfo)
    }
  },
  methods: {
    handleGetCode() {
      this.dialogVisible = true;
      code().then(resp => {
        let data = resp.data;
        this.base64Str = `data:image/png;base64,${data.img}`
      })
    }
  }
}
</script>

<style scoped>
.el-input {
  width: 300px;
}
</style>