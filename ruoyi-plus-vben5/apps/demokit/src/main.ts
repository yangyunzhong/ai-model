import { createApp } from 'vue';
import { createMemoryHistory, createRouter } from 'vue-router';

import '@vben/styles';
import '@st/platform-styles/antd';
import '../../web-antd/src/assets/iconfont/iconfont.css';

import App from './App.vue';
import './styles.css';

const router = createRouter({
  history: createMemoryHistory(),
  routes: [
    {
      component: App,
      path: '/:pathMatch(.*)*',
    },
  ],
});

createApp(App).use(router).mount('#app');
