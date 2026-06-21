const http = require('http');

function request(path) {
  return new Promise((resolve, reject) => {
    const req = http.get({ hostname: 'localhost', port: 8082, path: path, timeout: 10000 }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => resolve({ status: res.statusCode, data }));
    });
    req.on('error', reject);
    req.on('timeout', () => reject(new Error('timeout')));
  });
}

async function test() {
  try {
    console.log('测试分类API...');
    const result = await request('/api/question/categories');
    console.log('状态:', result.status);
    const json = JSON.parse(result.data);
    console.log('分类数:', json.data?.length);
    console.log('分类样例:', json.data?.slice(0, 3).map(c => c.name));
  } catch (e) {
    console.error('测试失败:', e.message);
  }
}

test();
