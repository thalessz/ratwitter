// Controlador dos posts

const Post = require('../models/Post');

exports.fetchPosts = (req,res)=>{
    Post.fetchAll((err,result)=>{
        if(err)return res.status(500).json({error:err.message});
        if(!result.length)return res.status(200).json({message:'Nenhum post encontrado'});
        return res.status(200).json(result)
    });
};

exports.addPost=(req,res)=>{
    const {content,user_id}=req.body;
    
    if(!content||!user_id) return res.status(400).json({message:'Conteúdo e user_id são obrigatórios'});

    Post.addPost({content,user_id},(err)=>{
        if(err)return res.status(500).json({error:err.message});
        return res.status(201).json({message:'Post adicionado com sucesso'});
    });
};

exports.likePost=(req,res)=>{
    const postID = req.params.post_id;
    const {user_id}= req.body;

    Post.likePost(postID,user_id,(err)=>{
        if(err)return res.status(500).json({error:err.message});
        return res.status(200).json({message:'Post curtido com sucesso'});
    })
};

exports.unlikePost = (req, res) => {
    const postId = req.params.post_id; // Obtém o ID do post a partir dos parâmetros da URL
    const userId = req.query.user_id; // Obtém o ID do usuário a partir dos parâmetros de consulta

    // Verifica se o user_id foi fornecido
    if (!userId) {
        return res.status(400).json({ error: 'user_id é obrigatório' });
    }

    // Chama a função para remover o like do banco de dados
    Post.unlikePost(postId, userId, (err) => {
        if (err) {
            console.error("Erro ao remover curtida:", err);
            return res.status(500).json({ error: err.message }); // Retorna um erro 500 se ocorrer um problema
        }
        return res.status(200).json({ message: 'Curtida removida com sucesso' }); // Retorna sucesso
    });
};

 exports.check_if_liked=(req,res)=>{
    const PostId = req.params.post_id;
    const {user_id}= req.body;

    Post.check_if_liked(user_id,PostId, (err, result)=>{
        if(err) return res.status(500).json({message:err.message});
        return res.status(200).json(result);
    })
 }