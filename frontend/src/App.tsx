import React, { useEffect, useState } from "react";

export default function App(): React.ReactElement {
  const [msg, setMsg] = useState<string>("");

  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then((r) => r.text())
      .then((t) => setMsg(t))
      .catch(() => setMsg("No backend response"));
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h1>MiniBuskingBig</h1>
      <p>Backend says: {msg}</p>
    </div>
  );
}
