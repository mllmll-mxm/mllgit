<template>
  <div>
    <div class="login_div" v-show="showLogin">
      <h3>登录</h3>
      <p v-show="showError">{{errorMessage}}</p>
      <input type="text" placeholder="请输入手机号或邮箱" v-model="user.username"/>
      <input type="password" placeholder="请输入密码" v-model="user.userpwd"/>
      <button v-on:click="login">登录</button>
      <span v-on:click="toRegister">没有账号？去注册</span>
    </div>

    <div class="register_div" v-show="showRegister">
      <h3>注册</h3>
      <p v-show="showError">{{errorMessage}}</p>
      <input type="text" placeholder="请输入手机号或邮箱" v-model="user.username" v-on:blur="checkValidName"/>
      <input type="password" placeholder="请输入密码" v-model="user.userpwd" v-on:blur="checkValidPwd"/>
      <input type="password" placeholder="请再次确认密码" v-model="repeatpwd" v-on:blur="checkRepeatPwd"/>
      <button v-on:click="register">注册</button>
      <span v-on:click="toLogin">已有帐号？马上登录</span>
    </div>
  </div>
</template>

<script>

  export default {
    name: "login",
    data() {
      return {
        showLogin: false,
        showRegister: true,
        showError: false,
        errorMessage: '',
        repeatpwd: '',
        nameFlag: false,
        pwdFlag: false,
        repeatFlag: false,
        user: {username: '', userpwd: '', email: '', phone: ''}
      }
    },
    methods: {
      checkValidName: function () {
        let emailRegex = /^([[A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
        let phoneRegex = /^1[34578]\d{9}$/;
        if (!(this.user.username != "" && (emailRegex.test(this.user.username) || phoneRegex.test(this.user.username)))) {
          this.errorMessage = '请输入手机号或邮箱';
          this.showError = true;
        } else {
          this.showError = false;
          this.nameFlag = true;
          if (emailRegex.test(this.user.username)) {
            this.user.email = this.user.username;
          } else
            this.user.phone = this.user.username;
        }
      },
      checkValidPwd: function () {
        let pwdRegex = /(?=.*[0-9])(?=.*[a-zA-Z]).{8,30}/;
        if (!pwdRegex.test(this.user.userpwd) && this.user.userpwd != "") {
          this.errorMessage = '密码必须包括字母，数字，至少8个字符';
          this.showError = true;
        } else {
          this.showError = false;
          this.pwdFlag = true;
        }
      },
      checkRepeatPwd: function () {
        if (this.user.userpwd != this.repeatpwd && this.repeatpwd != "") {
          this.errorMessage = '两次密码不一致，请重新输入';
          this.showError = true;
        } else {
          this.showError = false;
          this.repeatFlag = true;
        }
        if (this.repeatpwd == "" && this.pwdFlag) {
          this.errorMessage = '请输入确认密码';
          this.showError = true;
        }
      },
      register: function () {
        if (this.nameFlag && this.pwdFlag && this.repeatFlag) {
          //let uri = 'http://192.168.0.79:8889/add';
          const _this = this;
          this.$axios.post(this.global.registerURI,this.user).then(function(response){
            let result = response.data;
            if (result.code == "fail") {
              _this.errorMessage = result.data;
              _this.showError = true;
            }
            if (result.code == "success") {
              _this.user.username = '';
              _this.user.userpwd = '';
              _this.showRegister = false;
              _this.showLogin = true;
            }
          }).catch(function (err) {
              console.log(err);
          });
        }
      },
      login: function () {
        if (this.user.username == "" || this.user.userpwd == "") {
          this.errorMessage = '请输入用户名或密码';
          this.showError = true;
        } else {
          this.showError = false;
          this.nameFlag = true;
          this.pwdFlag = true;
        }
        if (this.nameFlag && this.pwdFlag) {
          //let uri = 'http://192.168.0.79:8889/foreLogin';
          const _this = this;
          this.$axios.post(this.global.loginURI,this.user).then(function(response){
            let result = response.data;
            if (result.code == 'success'){
               _this.global.token = result.data;
               _this.$cookies.set("username",_this.user.username,300);
              // _this.$cookies.set("token",result.data,300);
               const info = {name:_this.user.username,token:result.data};
               localStorage.setItem('userInfo',JSON.stringify(info));
              // console.log("cookie的username："+_this.$cookies.get("username")+",token:"+_this.$cookies.get("token"));
              console.log("登录成功");
              _this.$router.push({path:'/success'});
            }else{
              _this.errorMessage = '用户名或密码错误';
              _this.showError = true;
              console.log("登录失败");
            }
          });
        }
      },
      toRegister: function () {
        this.showLogin = false;
        this.showRegister = true;
      },
      toLogin: function () {
        this.showRegister = false;
        this.showLogin = true;
      }
    }
  }
</script>

<style scoped>
  .login_div {
    text-align: center;
  }

  input {
    display: block;
    width: 250px;
    height: 40px;
    line-height: 40px;
    margin: 0 auto;
    margin-bottom: 10px;
    outline: none;
    border: 1px solid #888;
    padding: 10px;
    box-sizing: border-box;
  }

  p {
    color: red;
  }

  button {
    display: block;
    width: 250px;
    height: 40px;
    line-height: 40px;
    margin: 0 auto;
    border: none;
    background-color: #41b883;
    color: #fff;
    font-size: 16px;
    margin-bottom: 5px;
  }
  button:hover{
    cursor: pointer;
  }

  span {
    cursor: pointer;
  }

  span:hover {
    color: #41b883;
  }
</style>
