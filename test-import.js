const http = require('http');
const fs = require('fs');
const path = require('path');

const filePath = 'doc/完整题库（带解析）.xlsx';
const fileData = fs.readFileSync(filePath);
const boundary = '----FormBoundary' + Date.now();

const postData = Buffer.concat([
  Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${path.basename(filePath)}"\r\nContent-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\r\n\r\n`),
  fileData,
  Buffer.from(`\r\n--${boundary}--\r\n`)
]);

const options = {
  hostname: 'localhost',
  port: 8082,
  path: '/api/import/questions',
  method: 'POST',
  headers: {
    'Content-Type': `multipart/form-data; boundary=${boundary}`,
    'Content-Length': postData.length
  },
  timeout: 300000 // 5分钟超时
};

console.log('开始上传文件，大小:', fileData.length, '字节');
console.log('时间:', new Date().toLocaleString());

const req = http.request(options, (res) => {
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
    console.log('响应状态:', res.statusCode);
    console.log('响应内容:', data);
    try {
      const result = JSON.parse(data);
      console.log('\n导入结果:');
      console.log('  总行数:', result.data?.totalRows);
      console.log('  成功数:', result.data?.successCount);
      console.log('  失败数:', result.data?.failCount);
      console.log('  错误信息:', result.data?.errorMessages?.slice(0, 10));
    } catch(e) {
      console.log('解析失败:', e.message);
    }
    console.log('结束时间:', new Date().toLocaleString());
  });
});

req.on('error', (e) => {
  console.error('请求失败:', e.message);
});

req.on('timeout', () => {
  console.error('请求超时');
  req.destroy();
});

req.write(postData);
req.end();
