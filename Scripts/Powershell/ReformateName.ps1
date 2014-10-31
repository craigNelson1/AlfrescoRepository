$a = Get-ChildItem 'C:\Users\Administrator\Desktop\Project Jackets2\Production' -recurse | Where-Object {$_.PSIsContainer -eq $True}

foreach ($item in $a){

$item1 = $item.Name.Substring(0,1)
if($item1.Equals("R")){
$numericValue = $item.Name.Substring(0,6)
}else{

$numericValue = $item.Name.Substring(0,5)
}
[Double]$number = 0
if ([Double]::TryParse($numericValue,[ref] $number)){

if($item1.Equals('R')){
$newFileName = $item.Name.SubString(6) + "."+ $numericValue;
}else{
$newFileName = $item.Name.SubString(5) + "."+ $numericValue;

}
Write-Output $newFileName
Rename-Item -path  $item.FullName -newName $newFileName

}

else

{
Write-Output "Did not make Any Changes for " + $item.FullName

}



try{


} catch {
##Write-Host "Did not Change Folder Name" + $item.Name
}

}