const mysql = require('mysql2/promise');

async function check() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    port: 3307,
    user: 'root',
    password: 'root',
    database: 'quiz_master'
  });

  try {
    const [qRows] = await connection.execute('SELECT COUNT(*) as count FROM question');
    const [oRows] = await connection.execute('SELECT COUNT(*) as count FROM question_option');
    const [cRows] = await connection.execute('SELECT COUNT(*) as count FROM category');

    console.log('题目数量:', qRows[0].count);
    console.log('选项数量:', oRows[0].count);
    console.log('分类数量:', cRows[0].count);

    const [sample] = await connection.execute('SELECT * FROM question LIMIT 3');
    console.log('\n样例题目:');
    sample.forEach(q => {
      console.log('  ID:', q.id, '内容:', q.content.substring(0, 50));
    });
  } finally {
    await connection.end();
  }
}

check().catch(console.error);
