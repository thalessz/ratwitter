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

exports.unlikePost=(req,res)=>{
    const postId=req.params.post_id;
    const {user_id}=req.body;
 
    Post.unlikePost(postId,user_id,(err)=>{
       if(err)return res.status(500).json({error:err.message});
       return res.status(200).json({message:'Curtida removida com sucesso'});
    })
 }

 exports.check_if_liked=(req,res)=>{
    const PostId = req.params.post_id;
    const {user_id}= req.body;

    Post.check_if_liked(user_id,PostId, (err, result)=>{
        const response = [
            {
                liked: result
            }
        ]
        if(err) return res.status(500).json({message:err.message});
        return res.status(200).json(response);
    })
 }