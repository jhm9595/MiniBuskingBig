import React, {useEffect, useState} from 'react'

export default function App(){
  const [msg, setMsg] = useState('')

  useEffect(()=>{
    fetch('/api/hello')
      .then(r=>r.text())
      .then(t=>setMsg(t))
      .catch(()=>setMsg('No backend response'))
  },[])

  return (
    <div style={{padding:20}}>
      <h1>MiniBuskingBig</h1>
      <p>Backend says: {msg}</p>
    </div>
  )
}
