const User = require('../models/User')

//trata as rotas. controladores.
exports.login = (req,res)=> {
    const {username, password} = req.body;
    if(!username || !password) return res.status(400).json({message: 'usuário e senha obrigatórios'});

    User.login(username,password, (err,result)=>{
        if(err) throw res.status(500).json({error: err.message});
        if (!result.length) return res.status(401).json({message: 'Login inválido'});
        return res.status(200).json(result[0]);
    });
}

exports.cadastro = (req,res) => {
    const {nome,username,password,email} = req.body;
    if(!nome || !username || !password || !email) return res.status(400).json({ message: 'Todos os campos são obrigatórios' });

    User.create({nome,username,password,email}, (err) => {
        if (err) return res.status(500).json({ error: err.message });
        return res.status(201).json({ message: 'Cadastro realizado com sucesso' });
    });
};

exports.fetchByID = (req,res) => {
    const uid = req.params.id;

    User.fetchByID(uid, (err,result)=>{
        if (err) return res.status(500).json({ error: err.message });
        if (!result.length) return res.status(401).json({message: 'Login inválido'});
        return res.status(200).json(result[0]);
    });
}

exports.fetchID = (req,res) => {
    const username = req.params.username;

    User.fetchByUsername(username, (err, result) =>{
        if (err) return res.status(500).json({ error: err.message });
        if(!result.length)return res.status(404).json({message:'Usuário não encontrado'});
        return res.status(200).json({user_id:result[0].id});
    })
}