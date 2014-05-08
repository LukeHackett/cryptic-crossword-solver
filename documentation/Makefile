report:
	@echo -n "compiling full report... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf pdfs/report.pdf
	@echo "done!"
	@make clean

books:
	for dir in `ls -d -- */`; do ( cd $$dir ; make ; make clean ; cd .. ); done

all:
	make books
	make report
	make clean

clean:
	@rm -rf out/
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned folder"
