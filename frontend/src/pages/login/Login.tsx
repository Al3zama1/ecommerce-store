import React from 'react'

import './Login.css'

type Props = {}

function Login({}: Props) {
  return (
    <div className='login'>
        {/* <img src='/online-shop.png' className='login__img' /> */}
        <div className='login__container'>
        <h1 className='login__title'>Sign In</h1>
        <form action="" className='login-form'>
            <label className='login__label'>Email address</label>
            <input className='login__input' type='email' />
            <label className='login__label'>Password</label>
            <input type='password' className='login__input' />
            <button type='submit' className='login__btn'>Login</button>
        </form>
        </div>
    </div>
  )
}

export default Login