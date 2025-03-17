export default async function Home() {
  const data = await fetch("https://zlotowka-621092586366.us-central1.run.app/test")
      .then((response) => response.text())
      .catch((error) => console.error(error));

  return (
      <div className="h-[200vh] flex flex-col justify-center items-center">
        <h1 className="text-4xl -mt-36 mb-4">Scrollable Hello World</h1>
        <h2>{data ? data : "Loading..."}</h2>
      </div>

  );
}
