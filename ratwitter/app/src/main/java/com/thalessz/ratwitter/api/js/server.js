const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const userRoutes = require('./routes/userRoutes');
const postRoutes = require('./routes/postRoutes');

const app = express();
app.use(cors());
app.use(bodyParser.json());

app.use('/users', userRoutes);app.use('/users',userRoutes); 
app.use('/posts',postRoutes); 

const port=process.env.PORT||5000;
app.listen(port,( )=>{
   console.log(`Servidor rodando na porta ${port}`);
});