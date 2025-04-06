import {NextResponse} from "next/server";

export async function GET(request: Request) {
  const url = new URL(request.url)
  const userId = url.searchParams.get('userId')

  const currentDate = new Date()
  const formattedDate = currentDate.toLocaleDateString('pl-PL')

  let responseObject = {
    value: 0,
    name: '',
    date: formattedDate,
  }

  if (userId === '1') {
    responseObject = {
      value: 1000,
      name: 'Zakupy spożywcze',
      date: formattedDate,
    }
  } else if (userId === '2') {
    responseObject = {
      value: 500,
      name: 'Elektronika',
      date: formattedDate,
    }
  }

  return NextResponse.json(responseObject)
}