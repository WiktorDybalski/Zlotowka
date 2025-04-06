import {NextResponse} from "next/server";

export async function GET(request: Request) {
  const url = new URL(request.url)
  const userId = url.searchParams.get('userId')

  if (userId === '1') {
    return NextResponse.json(58000)
  } else if (userId === '2') {
    return NextResponse.json(69999)
  }

  return NextResponse.json(0)
}